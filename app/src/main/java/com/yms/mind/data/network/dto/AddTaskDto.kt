package com.yms.mind.data.network.dto

/*
AddTaskDto - формат запроса в сеть для добавления задачи
 */

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AddTaskDto(
    @SerializedName("element") val element: TodoItemDto,
)