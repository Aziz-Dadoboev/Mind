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


data class TodoItemApi(
    val id: String,
    val text: String,
    val importance: String,
    val created_at: Long,
    val changed_at: Long,
    val last_updated_by: String
)

fun TodoItemApi.toDomain(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        priority = when (this.importance) {
            "low" -> Priority.LOW
            "important" -> Priority.HIGH
            else -> Priority.NORMAL
        },
        deadline = null,
        status = false,
        creationDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(this.created_at)),
        modificationDate = this.changed_at.let { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(it)) }
    )
}


fun TodoItem.toApi(): TodoItemApi {
    return TodoItemApi(
        id = id,
        text = text,
        importance = when (priority) {
            Priority.LOW -> "low"
            Priority.HIGH -> "important"
            else -> "basic"
        },
        created_at = creationDate.toLong(),
        changed_at = modificationDate?.toLongOrNull() ?: creationDate.toLong(),
        last_updated_by = ""
    )
}