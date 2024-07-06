package com.yms.mind.network
import com.yms.mind.data.TodoItemApi
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class TodoListResponse(
    val status: String,
    val list: List<TodoItemApi>,
    val revision: Int
)

interface ApiService {
    @GET("list")
    fun getTasks(@Header("Authorization") token: String): Call<TodoListResponse>

    @PATCH("list")
    fun updateTasks(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body tasks: List<TodoItemApi>
    ): Call<TodoListResponse>

    @GET("list/{id}")
    fun getTask(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<TodoItemApi>

    @POST("list")
    fun addTask(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body task: TodoItemApi
    ): Call<TodoItemApi>

    @PUT("list/{id}")
    fun updateTask(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body task: TodoItemApi
    ): Call<TodoItemApi>

    @DELETE("list/{id}")
    fun deleteTask(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<TodoItemApi>
}