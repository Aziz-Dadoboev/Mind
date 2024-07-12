package com.yms.mind.data.network.dto

/*
ListDto - формат списка для запросов в сеть
 */

import com.google.gson.annotations.SerializedName

data class ListDto(
    @SerializedName("list") val list: List<TodoItemDto>,
    @SerializedName("revision") val revision: Int,
    @SerializedName("status") val status: String
)
