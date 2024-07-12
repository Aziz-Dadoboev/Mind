package com.yms.mind.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yms.mind.data.model.Priority
import com.yms.mind.data.model.TodoItem
import com.yms.mind.data.repository.TodoItemsRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class EditViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {

    private var _todoItem: TodoItem? = null
    val todoItem: TodoItem? get() = _todoItem

    fun saveTask(idArg: String?, text: String, priority: Priority, deadline: LocalDateTime?) {
        viewModelScope.launch {
            if (idArg != null) {
                todoItemsRepository.updateTask(idArg, text, priority, deadline)
            } else {
                todoItemsRepository.addTask(text, priority, deadline)
            }
        }
    }

    fun setTodoItem(idArg: String?) {
        _todoItem = if (idArg != null) {
            todoItemsRepository.findTodoItemById(idArg)
        } else {
            null
        }

        if (this.todoId != idArg) {
            this.todoId = idArg
            text = _todoItem?.text
            priority = _todoItem?.priority
            deadline = _todoItem?.deadline
        }
    }


    fun deleteTask(idArg: String?) {
        viewModelScope.launch {
            if (idArg != null) {
                todoItemsRepository.deleteTask(idArg)
            }
        }
    }

    private var todoId: String? = null
    var text: String? = null
    var priority: Priority? = null
    var deadline: LocalDateTime? = null

}