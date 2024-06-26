package com.yms.mind.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yms.mind.data.TodoItem
import com.yms.mind.data.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel: ViewModel() {
    private val repository = TodoItemsRepository()

    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> get() = _todoItems

    init {
        loadTodoItems()
    }

    private fun loadTodoItems() {
        viewModelScope.launch {
            _todoItems.value = repository.getTodoItems()
        }
    }

    private suspend fun loadVisibleItems() {
        _todoItems.value = repository.getUndoneTasks()
    }

    fun setVisible(isVisible: Boolean) {
        viewModelScope.launch {
            if (!isVisible) loadVisibleItems()
            else loadTodoItems()
        }
    }

    fun checkItem(id: String, status: Boolean, isVisible: Boolean) {
        viewModelScope.launch {
            repository.checkItem(id, status)
            if (!isVisible) loadVisibleItems()
            else loadTodoItems()
        }
    }

    suspend fun generateId(): String {
        return repository.generateId()
    }

    fun addTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.addTodoItem(todoItem)
            loadTodoItems()
        }
    }

    fun deleteItem(todoItemId: String) {
        viewModelScope.launch {
            repository.deleteItem(todoItemId)
            loadTodoItems()
        }
    }

    suspend fun getItem(todoItemId: String): TodoItem? {
        return repository.getItem(todoItemId)
    }
}