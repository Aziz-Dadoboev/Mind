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

    fun checkItem(id: String, status: Boolean) {
        repository.checkItem(id, status)
        loadTodoItems()
    }

    fun generateId(): String {
        return repository.generateId()
    }

    fun addTodoItem(todoItem: TodoItem) {
        repository.addTodoItem(todoItem)
        loadTodoItems()
    }
}