package com.yms.mind.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yms.mind.data.TodoItem
import com.yms.mind.data.TodoItemsRepository
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

    init {
        loadAllTodoItems()
        _todoItems.value = _allTodoItems.value
    }

    private fun loadAllTodoItems() {
        customScope.launch {
            try {
                _allTodoItems.value = withContext(Dispatchers.IO) { repository.getTodoItems() }
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
            withContext(Dispatchers.Main) { // Без этого пока никак(
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
}