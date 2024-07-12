package com.yms.mind.adapters

import androidx.recyclerview.widget.DiffUtil
import com.yms.mind.data.model.TodoItem

class TodoListDiffUtilCallback : DiffUtil.ItemCallback<TodoItem>() {

    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}
