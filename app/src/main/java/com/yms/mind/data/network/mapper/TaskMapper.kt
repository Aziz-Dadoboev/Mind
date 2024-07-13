package com.yms.mind.data.network.mapper

import java.time.LocalDateTime
import java.time.ZoneOffset
import android.os.Build
import com.yms.mind.data.model.TodoItem
import com.yms.mind.data.model.fromStringToPriority
import com.yms.mind.data.network.dto.AddTaskDto
import com.yms.mind.data.network.dto.ListDto
import com.yms.mind.data.network.dto.TodoItemDto


object TaskMapper {
    fun fromDto(dto: TodoItemDto): TodoItem {

        return TodoItem(
            id = dto.id,
            text = dto.text,
            priority = fromStringToPriority(dto.importance),
            deadline = dto.deadline?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) },
            status = dto.done,
            creationDate = LocalDateTime.ofEpochSecond(dto.createdAt, 0, ZoneOffset.UTC),
            modificationDate = dto.changedAt.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
        )
    }

    fun toDto(item: TodoItem): TodoItemDto {
        return TodoItemDto(
            id = item.id,
            text = item.text,
            importance = item.priority.name.lowercase(),
            deadline = item.deadline?.toEpochSecond(ZoneOffset.UTC),
            done = item.status,
            color = "#FFFFFF",
            createdAt = item.creationDate.toEpochSecond(ZoneOffset.UTC),
            changedAt = item.modificationDate?.toEpochSecond(ZoneOffset.UTC)
                ?: item.creationDate.toEpochSecond(ZoneOffset.UTC),
            lastUpdatedBy = Build.ID

        )
    }

    fun toAddDto(item: TodoItem): AddTaskDto {
        return AddTaskDto(
            element = TodoItemDto(
                id = item.id,
                text = item.text,
                importance = item.priority.name.lowercase(),
                deadline = item.deadline?.toEpochSecond(ZoneOffset.UTC),
                done = item.status,
                color = "#FFFFFF",
                createdAt = item.creationDate.toEpochSecond(ZoneOffset.UTC),
                changedAt = item.modificationDate?.toEpochSecond(ZoneOffset.UTC)
                    ?: item.creationDate.toEpochSecond(ZoneOffset.UTC),
                lastUpdatedBy = Build.ID
            )
        )
    }

    fun toListDto(items: List<TodoItemDto>, revision: Int): ListDto {
        return  ListDto(
            status = "ok",
            list = items,
            revision = revision
        )
    }

}