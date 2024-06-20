package com.yms.mind.data

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: String?,
    val status: Boolean,
    val creationDate: String,
    val modificationDate: String?
)

enum class Priority {
    LOW,
    NORMAL,
    HIGH
}