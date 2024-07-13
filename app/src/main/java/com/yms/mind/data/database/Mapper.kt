package com.yms.mind.data.database

import com.yms.mind.data.database.entities.TodoItemEntity
import com.yms.mind.data.model.TodoItem

fun TodoItemEntity.toDomainModel() = TodoItem(
    id = id,
    text = text,
    priority = priority,
    deadline = deadline,
    status = done,
    creationDate = creationDate,
    modificationDate = modificationDate
)

fun TodoItem.toEntity() = TodoItemEntity(
    id = id,
    text = text,
    priority = priority,
    deadline = deadline,
    done = status,
    creationDate = creationDate,
    modificationDate = modificationDate
)