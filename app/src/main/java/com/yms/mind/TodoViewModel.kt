package com.yms.mind

import androidx.lifecycle.ViewModel
import com.yms.mind.data.TodoItem
import com.yms.mind.data.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TodoViewModel: ViewModel() {
    private val repository = TodoItemsRepository()

    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> get() = _todoItems

    init {
        loadTodoItems()
    }

    private fun loadTodoItems() {
        _todoItems.value = repository.getTodoItems()
    }

    private fun loadVisibleItems() {
        _todoItems.value = repository.getUndoneTasks()
    }

    fun setVisible(isVisible: Boolean) {
        if (!isVisible) loadVisibleItems()
        else loadTodoItems()
    }

    fun checkItem(id: String, status: Boolean, isVisible: Boolean) {
        repository.checkItem(id, status)
        if (!isVisible) loadVisibleItems()
        else loadTodoItems()
    }

    fun generateId(): String {
        return repository.generateId()
    }

    fun addTodoItem(todoItem: TodoItem) {
        repository.addTodoItem(todoItem)
        loadTodoItems()
    }

    fun deleteItem(todoItemId: String) {
        repository.deleteItem(todoItemId)
        loadTodoItems()
    }

    fun getItem(todoItemId: String): TodoItem? {
        return repository.getItem(todoItemId)
    }
}