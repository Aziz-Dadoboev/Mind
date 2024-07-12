package com.yms.mind.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yms.mind.data.model.TodoItem
import com.yms.mind.data.repository.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoItemsRepository
): ViewModel() {
//    private val supervisorJob = SupervisorJob()
//    private val customScope = CoroutineScope(supervisorJob + Dispatchers.Main)
//
//    private val _allTodoItems = MutableStateFlow<List<TodoItem>>(emptyList())
//    private val allTodoItems: StateFlow<List<TodoItem>> get() = _allTodoItems
//
//    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
//    val todoItems: StateFlow<List<TodoItem>> get() = _todoItems
//
//    private val _item = MutableStateFlow<TodoItem?>(null)
//    val item: StateFlow<TodoItem?> get() = _item.asStateFlow()
//
    private val _errorMessages = MutableStateFlow<String?>(null)
    val errorMessages: StateFlow<String?> get() = _errorMessages
//
//    private var todoItemsVisible: Boolean = true
//    private var revision: Int = 0

    init {
        loadAllTodoItems()
//        _todoItems.value = _allTodoItems.value
    }

    private val _showCompletedTasks = MutableStateFlow(true)
    val showCompletedTasks = _showCompletedTasks.asStateFlow()

    val currentTasks: StateFlow<List<TodoItem>> = combine(
        showCompletedTasks,
        repository.todoItems,
        repository.uncompletedTodoItems
    ) { showCompleted, allTasks, uncompletedTasks ->
        if (showCompleted) {
            allTasks
        } else {
            uncompletedTasks
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    val numberOfCompletedTasks: StateFlow<Int> = repository.numCompletedTodoItems

    fun toggleShowCompletedTasks() {
        _showCompletedTasks.value = !_showCompletedTasks.value
    }

    fun changeTaskStatus(taskId: String) {
        viewModelScope.launch {
            repository.changeTodoItemStatus(taskId)
        }
    }

    private fun loadAllTodoItems() {
        viewModelScope.launch {
            try {
                repository.getTasks()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(e: Exception) {
        Log.e("TodoViewModel", "Error: ${e.message}", e)
        _errorMessages.value = "An error occurred: ${e.message}"
    }

    fun clearErrorMessage() {
        _errorMessages.value = null
    }
}