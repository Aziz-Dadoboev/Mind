package com.yms.mind.data.model

import java.time.LocalDateTime

/**
TodoItem - модель хранения задачи
 */

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: LocalDateTime?,
    val status: Boolean,
    val creationDate: LocalDateTime,
    val modificationDate: LocalDateTime?
)

/**
Priority - модель хранения важности задачи
 */

enum class Priority {
    LOW,
    BASIC,
    HIGH
}

fun fromStringToPriority(priority: String): Priority {
    return when(priority) {
        "low" -> Priority.LOW
        "high" -> Priority.HIGH
        else -> Priority.BASIC
    }
}
