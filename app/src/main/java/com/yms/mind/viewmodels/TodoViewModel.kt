package com.yms.mind.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yms.mind.data.TodoItem
import com.yms.mind.data.TodoItemsRepository
import com.yms.mind.data.toApi
import com.yms.mind.network.TOKEN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class TodoViewModel: ViewModel() {
    private val supervisorJob = SupervisorJob()
    private val customScope = CoroutineScope(supervisorJob + Dispatchers.Main)

    private val repository = TodoItemsRepository()

    private val _allTodoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    private val allTodoItems: StateFlow<List<TodoItem>> get() = _allTodoItems

    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> get() = _todoItems

    private val _item = MutableStateFlow<TodoItem?>(null)
    val item: StateFlow<TodoItem?> get() = _item.asStateFlow()

    private val _errorMessages = MutableStateFlow<String?>(null)
    val errorMessages: StateFlow<String?> get() = _errorMessages

    private var todoItemsVisible: Boolean = true
    private var revision: Int = 0

    init {
        loadAllTodoItems()
        _todoItems.value = _allTodoItems.value
    }

    private fun loadAllTodoItems() {
        viewModelScope.launch {
            try {
                val tasks = repository.getTasks(TOKEN)
                Log.d("TodoViewModel", "Loaded tasks: ${tasks?.size ?: 0}")
                tasks?.forEach { Log.d("TodoViewModel", "Task: $it") }
                _allTodoItems.value = tasks ?: emptyList()
                updateVisibleItems()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateVisibleItems() {
        _todoItems.value = if (todoItemsVisible) {
            _allTodoItems.value
        } else {
            _allTodoItems.value.filter { !it.status }
        }
    }

    fun setItemsVisible(isVisible: Boolean) {
        this.todoItemsVisible = isVisible
        viewModelScope.launch {
            updateVisibleItems()
        }
    }

    fun checkItem(id: String, status: Boolean) {
        customScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.checkItem(id, status)
                }
                loadAllTodoItems()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    fun addTodoItem(todoItem: TodoItem) {
        customScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.addTodoItem(todoItem)
                }
                loadAllTodoItems()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun deleteItem(todoItemId: String) {
        customScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.deleteItem(todoItemId)
                }
                loadAllTodoItems()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    suspend fun fetchItem(todoItemId: String): TodoItem? {
        return try {
            withContext(Dispatchers.Main) {
                repository.getItem(todoItemId)
            }
        } catch (e: Exception) {
            handleError(e)
            null
        }
    }

    fun getCompletedTasksCount(): Int {
        return allTodoItems.value.count { it.status }
    }

    override fun onCleared() {
        super.onCleared()
        supervisorJob.cancel()
    }

    private fun handleError(e: Exception) {
        Log.e("TodoViewModel", "Error: ${e.message}", e)
        _errorMessages.value = "An error occurred: ${e.message}"
    }

    fun clearErrorMessage() {
        _errorMessages.value = null
    }

    fun updateTasks() {
        viewModelScope.launch {
            try {
                val updatedTasks = repository.updateTasks(TOKEN, revision, _allTodoItems.value)
                Log.d("TodoViewModel", "Updated tasks: ${updatedTasks?.size ?: 0}")
                updatedTasks?.forEach { Log.d("TodoViewModel", "Task: $it") }
                _allTodoItems.value = updatedTasks ?: emptyList()
                updateVisibleItems()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun getTask(id: String) {
        viewModelScope.launch {
            try {
                val token = "your_token_here"
                val task = repository.getTask(token, id)
                Log.d("TodoViewModel", "Fetched task: $task")
                _item.value = task
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun addTask(task: TodoItem) {
        viewModelScope.launch {
            try {
                val taskApi = task.toApi()
                val addedTask = repository.addTask(TOKEN, taskApi)
                if (addedTask != null) {
                    repository.setCurrentRevision(repository.getCurrentRevision() + 1)
                    loadAllTodoItems()
                } else {
                    Log.e("TodoViewModel", "Error adding task")
                }
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Exception: ${e.message}", e)
            }
        }
    }

    fun updateTask(id: String, task: TodoItem) {
        viewModelScope.launch {
            try {
                val updatedTask = repository.updateTask(TOKEN, revision, id, task)
                Log.d("TodoViewModel", "Updated task: $updatedTask")
                if (updatedTask != null) {
                    _allTodoItems.value = _allTodoItems.value.map {
                        if (it.id == id) updatedTask else it
                    }
                    updateVisibleItems()
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            try {
                val deletedTask = repository.deleteTask(TOKEN, id)
                Log.d("TodoViewModel", "Deleted task: $deletedTask")
                if (deletedTask != null) {
                    _allTodoItems.value = _allTodoItems.value.filter { it.id != id }
                    updateVisibleItems()
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

}