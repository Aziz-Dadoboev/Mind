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

    private val _errorMessages = MutableStateFlow<String?>(null)
    val errorMessages: StateFlow<String?> get() = _errorMessages

    init {
        loadAllTodoItems()
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