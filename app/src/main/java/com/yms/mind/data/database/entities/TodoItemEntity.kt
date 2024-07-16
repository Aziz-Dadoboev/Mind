package com.yms.mind.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yms.mind.data.model.Priority
import java.time.LocalDateTime

@Entity(tableName = "todo_items")
data class TodoItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "priority")
    val priority: Priority,
    @ColumnInfo(name = "deadline")
    val deadline: LocalDateTime?,
    @ColumnInfo(name = "status")
    val done: Boolean,
    @ColumnInfo(name = "created_at")
    val creationDate: LocalDateTime,
    @ColumnInfo(name = "updated_at")
    val modificationDate: LocalDateTime?,
)