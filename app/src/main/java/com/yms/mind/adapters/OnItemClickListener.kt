package com.yms.mind.adapters

import com.yms.mind.data.model.TodoItem

interface OnItemClickListener {
    fun onItemClick(todoItem: TodoItem)
}