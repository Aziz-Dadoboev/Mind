package com.yms.mind.adapters

import com.yms.mind.data.TodoItem

interface OnItemClickListener {
    fun onItemClick(todoItem: TodoItem)
}