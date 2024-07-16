package com.yms.mind.data.repository

import android.content.SharedPreferences
import com.yms.mind.data.database.dao.TodoItemDao
import com.yms.mind.data.database.toDomainModel
import com.yms.mind.data.database.toEntity
import com.yms.mind.data.model.Priority
import com.yms.mind.data.model.TodoItem
import com.yms.mind.data.network.ApiService
import com.yms.mind.data.network.mapper.TaskMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class TodoItemsRepository(
    private val todoApiService: ApiService,
    private val coroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val todoItemDao: TodoItemDao,
    sharedPreferences: SharedPreferences
) {
    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> = _todoItems
    private val _currentRevision = MutableStateFlow(sharedPreferences.getInt("revision", 0))

    val numCompletedTodoItems: StateFlow<Int> =
        _todoItems.map { items -> items.count { it.status } }
            .stateIn(coroutineScope, SharingStarted.Lazily, 0)

    val uncompletedTodoItems: StateFlow<List<TodoItem>> =
        _todoItems.map { items -> items.filter { !it.status } }
            .stateIn(coroutineScope, SharingStarted.Lazily, emptyList())

    fun findTodoItemById(todoId: String): TodoItem? {
        return todoItems.value.find { it.id == todoId }
    }

    private val editor = sharedPreferences.edit()

    init {
        coroutineScope.launch {
            _currentRevision.collect {
                editor.putInt("revision", it).apply()
            }
        }
    }

    private suspend fun <T> performNetworkRequest(
        request: suspend () -> T,
        onSuccess: (T) -> Unit,
        maxRetries: Long = 3,
        retryDelay: Long = 1000L
    ) = flow {
        val response = request()
        emit(response)
    }.retryWhen { cause, attempt ->
        if (cause is IOException && attempt < maxRetries) {
            delay(retryDelay)
            true
        } else {
            false
        }
    }.collect { response ->
        onSuccess(response)
    }

    suspend fun getTasks() {
        withContext(coroutineScope.coroutineContext + ioDispatcher) {
            _todoItems.value = todoItemDao.getTodoItems().map { it.toDomainModel() }
            val response = todoApiService.getTodoList()
            if (_todoItems.value.isNotEmpty() && _currentRevision.value == response.revision) {
                _currentRevision.value = response.revision
                val list = _todoItems.value.map { TaskMapper.toDto(it) }
                val responsePatch = todoApiService.patchTodoList(
                    _currentRevision.value, TaskMapper.toListDto(list, _currentRevision.value)
                )
                _currentRevision.value = responsePatch.revision
                val newList = responsePatch.list.map { TaskMapper.fromDto(it) }
                _todoItems.value = newList
                updateDataBase(newList)
            } else {
                _currentRevision.value = response.revision
                val newList = response.list.map { TaskMapper.fromDto(it) }
                _todoItems.value = newList
                updateDataBase(newList)
            }
        }
    }

    private suspend fun updateDataBase(newList: List<TodoItem>) {
        withContext(coroutineScope.coroutineContext + ioDispatcher) {
            todoItemDao.deleteAll()
            todoItemDao.insertTodoItems(newList.map { it.toEntity() })
        }
    }

    suspend fun addTask(
        taskText: String,
        priority: Priority,
        deadline: LocalDateTime?
    ) {
        withContext(coroutineScope.coroutineContext + ioDispatcher) {
            val newTask = TodoItem(
                id = UUID.randomUUID().toString(),
                text = taskText,
                priority = priority,
                deadline = deadline,
                status = false,
                creationDate = LocalDateTime.now(),
                modificationDate = null
            )

            todoItemDao.insertTodoItem(newTask.toEntity())
            _todoItems.value += newTask

            performNetworkRequest(request = {
                todoApiService.addTodoItem(_currentRevision.value, TaskMapper.toAddDto(newTask))
            }, onSuccess = { response ->
                _currentRevision.value = response.revision
            })
        }
    }

    suspend fun updateTask(
        taskId: String,
        text: String,
        importance: Priority,
        deadline: LocalDateTime?
    ) {
        withContext(coroutineScope.coroutineContext + ioDispatcher) {
            var newTask: TodoItem? = null
            val updatedList = _todoItems.value.map { item ->
                if (item.id == taskId) {
                    newTask = item.copy(
                        text = text,
                        priority = importance,
                        deadline = deadline,
                        modificationDate = LocalDateTime.now()
                    )
                    newTask!!
                } else {
                    item
                }
            }

            if (newTask != null) {
                todoItemDao.updateTodoItem(newTask!!.toEntity())
                _todoItems.value = updatedList
                performNetworkRequest(request = {
                    todoApiService.updateTodoItem(
                        _currentRevision.value, taskId, TaskMapper.toAddDto(newTask!!)
                    )
                }, onSuccess = { response ->
                    _currentRevision.value = response.revision
                })
            }
        }
    }

    suspend fun deleteTask(
        id: String
    ) {
        withContext(coroutineScope.coroutineContext + ioDispatcher) {
            val task = _todoItems.value.find { it.id == id }
            task?.let {
                val updatedList = _todoItems.value.filter { it.id != id }
                _todoItems.value = updatedList
                launch {
                    todoItemDao.deleteTodoItem(task.toEntity())
                }
                performNetworkRequest(request = {
                    todoApiService.deleteTodoItem(
                        _currentRevision.value, id
                    )
                }, onSuccess = { response -> _currentRevision.value = response.revision })
            }
        }
    }

    suspend fun changeTodoItemStatus(todoId: String) {
        withContext(coroutineScope.coroutineContext + ioDispatcher) {
            var newTask: TodoItem? = null
            val updatedList = _todoItems.value.map { item ->
                if (item.id == todoId) {
                    newTask = item.copy(status = !item.status)
                    newTask!!
                } else {
                    item
                }
            }
            if (newTask != null) {
                todoItemDao.updateTodoItem(newTask!!.toEntity())
                _todoItems.value = updatedList
                performNetworkRequest(request = {
                    todoApiService.updateTodoItem(
                        _currentRevision.value, todoId, TaskMapper.toAddDto(newTask!!)
                    )
                }, onSuccess = { response ->
                    _currentRevision.value = response.revision
                })
            }
        }
    }
}
