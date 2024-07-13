package com.yms.mind.data.network
import com.yms.mind.data.network.dto.AddTaskDto
import com.yms.mind.data.network.dto.ListDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("list")
    suspend fun getTodoList(): ListDto

    @POST("list")
    suspend fun addTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: AddTaskDto,
    ): ListDto

    @PUT("list/{id}")
    suspend fun updateTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body element: AddTaskDto,
    ): ListDto

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
    ): ListDto

    @PATCH("list")
    suspend fun patchTodoList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body list: ListDto,
    ): ListDto
}