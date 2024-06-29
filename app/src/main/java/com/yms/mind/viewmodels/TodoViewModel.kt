package com.yms.mind.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yms.mind.data.TodoItem
import com.yms.mind.data.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TodoViewModel: ViewModel() {
    private val repository = TodoItemsRepository()

    private val _allTodoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    private val allTodoItems: StateFlow<List<TodoItem>> get() = _allTodoItems

    // Отображаемый список
    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> get() = _todoItems

    private val _item = MutableStateFlow<TodoItem?>(null)
    val item: StateFlow<TodoItem?> get() = _item.asStateFlow()

    private var todoItemsVisible: Boolean = true

    init {
        loadAllTodoItems()
        _todoItems.value = _allTodoItems.value
    }

    private fun loadAllTodoItems() {
        viewModelScope.launch {
            _allTodoItems.value = repository.getTodoItems()
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
        viewModelScope.launch {
            repository.checkItem(id, status)
            loadAllTodoItems()
        }
        updateVisibleItems()
    }

    fun generateId(): String {
//        return repository.generateId()
        return UUID.randomUUID().toString()
    }

    fun addTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.addTodoItem(todoItem)
            loadAllTodoItems()
        }
        updateVisibleItems()
    }

    fun deleteItem(todoItemId: String) {
        viewModelScope.launch {
            repository.deleteItem(todoItemId)
            loadAllTodoItems()
        }
        updateVisibleItems()
    }

    suspend fun fetchItem(todoItemId: String): TodoItem? {
        return repository.getItem(todoItemId)
    }

    fun getCompletedTasksCount(): Int {
        return allTodoItems.value.count { it.status }
    }

}