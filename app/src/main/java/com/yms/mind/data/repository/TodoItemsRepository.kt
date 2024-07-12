package com.yms.mind.data.repository

import android.util.Log
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
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class TodoItemsRepository(
    private val todoApiService: ApiService,
    private val coroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) {
    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> = _todoItems
    private var currentRevision: Int = 0

    val numCompletedTodoItems: StateFlow<Int> =
        _todoItems.map { items -> items.count { it.status } }
            .stateIn(coroutineScope, SharingStarted.Lazily, 0)

    val uncompletedTodoItems: StateFlow<List<TodoItem>> =
        _todoItems.map { items -> items.filter { !it.status } }
            .stateIn(coroutineScope, SharingStarted.Lazily, emptyList())

    fun findTodoItemById(todoId: String): TodoItem? {
        return todoItems.value.find { it.id == todoId }
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
            performNetworkRequest(
                request = {
                    todoApiService.getTodoList()
                },
                onSuccess = { response ->
                    _todoItems.value = response.list.map { TaskMapper.fromDto(it) }
                    currentRevision = response.revision
                    Log.d("Request", " curr revision: $currentRevision")
                }
            )
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

            performNetworkRequest(
                request = {
                    todoApiService.addTodoItem(currentRevision, TaskMapper.toAddDto(newTask))
                },
                onSuccess = { response ->
                    _todoItems.value += newTask
                    currentRevision = response.revision
                }
            )
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
                performNetworkRequest(
                    request = {
                        todoApiService.updateTodoItem(
                            currentRevision,
                            taskId,
                            TaskMapper.toAddDto(newTask!!)
                        )
                    },
                    onSuccess = { response ->
                        _todoItems.value = updatedList
                        currentRevision = response.revision
                    }
                )
            }
        }
    }

    suspend fun deleteTask(
        id: String
    ){
        withContext(coroutineScope.coroutineContext + ioDispatcher) {
            performNetworkRequest(
                request = {
                    todoApiService.deleteTodoItem(currentRevision, id)
                },
                onSuccess = { response ->
                    _todoItems.value = _todoItems.value.filter { it.id != id }
                    currentRevision = response.revision
                }
            )
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
                performNetworkRequest(
                    request = {
                        Log.d("Request", "current 2 revision: $currentRevision")
                        todoApiService.updateTodoItem(
                            currentRevision,
                            todoId,
                            TaskMapper.toAddDto(newTask!!)
                        )
                    },
                    onSuccess = { response ->
                        _todoItems.value = updatedList
                        currentRevision = response.revision
                    }
                )
            }
        }
    }
}
