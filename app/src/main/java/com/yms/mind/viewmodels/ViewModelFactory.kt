package com.yms.mind.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yms.mind.data.repository.TodoItemsRepository

class ViewModelFactory(
    private val todoItemsRepository: TodoItemsRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(
                todoItemsRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditViewModel(
                todoItemsRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}