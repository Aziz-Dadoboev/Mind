package com.yms.mind.data

import android.util.Log
import com.yms.mind.network.RetrofitInstance.apiService
import com.yms.mind.network.TodoListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.UUID

class TodoItemsRepository {
    private val todoItems = HashMap<String, TodoItem>()
    private var currentRevision: Int = 0

    suspend fun getTasks(token: String): List<TodoItem>? {
        return withContext(Dispatchers.IO) {
            val response: Response<TodoListResponse> = apiService.getTasks("Bearer $token").execute()
            if (response.isSuccessful) {
                val tasks = response.body()?.list?.map { it.toDomain() }
                Log.d("TodoItemsRepository", "Fetched tasks: ${tasks?.size ?: 0}")
                tasks
            } else {
                Log.e("TodoItemsRepository", "Error fetching tasks: ${response.code()} - ${response.message()}")
                null
            }
        }
    }

//    private val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
//
//    init {
//        todoItems["1"] = TodoItem("1", "Buy groceries", Priority.NORMAL, "2022-09-30", false, "2022-09-27", null)
//        todoItems["2"] = TodoItem("2", "Finish report blah blah  blah blah  blah blah  blah blah", Priority.HIGH, "2022-10-05", false, "2022-09-27", null)
//        todoItems["3"] = TodoItem("3", "Call mom", Priority.LOW, null, true, "2022-09-28", null)
//        todoItems["4"] = TodoItem("4", "Go for a run", Priority.NORMAL, "2022-09-29", false, "2022-09-27", null)
//        todoItems["5"] = TodoItem("5", "Read a book", Priority.LOW, null, false, "2022-09-28", null)
//        todoItems["6"] = TodoItem("6", longText, Priority.LOW, null, false, "2022-09-28", null)
//        todoItems["7"] = TodoItem("7", "Renew gym membership", Priority.NORMAL, "2022-10-15", false, "2022-09-27", null)
//        todoItems["8"] = TodoItem("8", "Prepare presentation slides", Priority.HIGH, "2022-10-10", false, "2022-09-27", null)
//        todoItems["9"] = TodoItem("9", "Pay utility bills", Priority.LOW, null, true, "2022-09-30", null)
//        todoItems["10"] = TodoItem("10", "Attend online webinar", Priority.NORMAL, "2022-10-03", false, "2022-09-27", null)
//    }

    fun setCurrentRevision(revision: Int) {
        currentRevision = revision
    }

    suspend fun updateTasks(token: String, revision: Int, tasks: List<TodoItem>): List<TodoItem>? {
        return withContext(Dispatchers.IO) {
            val apiTasks = tasks.map { it.toApi() }
            val response: Response<TodoListResponse> = apiService.updateTasks("Bearer $token", revision, apiTasks).execute()
            if (response.isSuccessful) {
                val updatedTasks = response.body()?.list?.map { it.toDomain() }
                Log.d("TodoItemsRepository", "Updated tasks: ${updatedTasks?.size ?: 0}")
                updatedTasks
            } else {
                Log.e("TodoItemsRepository", "Error updating tasks: ${response.code()} - ${response.message()}")
                null // Handle error
            }
        }
    }

    suspend fun getTask(token: String, id: String): TodoItem? {
        return withContext(Dispatchers.IO) {
            val response: Response<TodoItemApi> = apiService.getTask("Bearer $token", id).execute()
            if (response.isSuccessful) {
                val task = response.body()?.toDomain()
                Log.d("TodoItemsRepository", "Fetched task: $task")
                task
            } else {
                Log.e("TodoItemsRepository", "Error fetching task: ${response.code()} - ${response.message()}")
                null // Handle error
            }
        }
    }

    suspend fun addTask(token: String, task: TodoItemApi): TodoItem? {
        return withContext(Dispatchers.IO) {
            val response = apiService.addTask("Bearer $token", currentRevision, task).execute()
            if (response.isSuccessful) {
                val addedTask = response.body()?.toDomain()
                Log.d("TodoItemsRepository", "Added task: $addedTask")
                addedTask
            } else {
                Log.e("TodoItemsRepository",
                    "Error adding task: ${response.code()} - ${response.message()} - $currentRevision")
                null // Handle error
            }
        }
    }

    suspend fun updateTask(token: String, revision: Int, id: String, task: TodoItem): TodoItem? {
        return withContext(Dispatchers.IO) {
            val apiTask = task.toApi()
            val response: Response<TodoItemApi> = apiService.updateTask("Bearer $token", revision, id, apiTask).execute()
            if (response.isSuccessful) {
                val updatedTask = response.body()?.toDomain()
                Log.d("TodoItemsRepository", "Updated task: $updatedTask")
                updatedTask
            } else {
                Log.e("TodoItemsRepository", "Error updating task: ${response.code()} - ${response.message()}")
                null // Handle error
            }
        }
    }

    suspend fun deleteTask(token: String, id: String): TodoItem? {
        return withContext(Dispatchers.IO) {
            val response: Response<TodoItemApi> = apiService.deleteTask("Bearer $token", id).execute()
            if (response.isSuccessful) {
                val deletedTask = response.body()?.toDomain()
                Log.d("TodoItemsRepository", "Deleted task: $deletedTask")
                deletedTask
            } else {
                Log.e("TodoItemsRepository", "Error deleting task: ${response.code()} - ${response.message()}")
                null // Handle error
            }
        }
    }

    suspend fun getTodoItems(): List<TodoItem> {
        return todoItems.values.toList()
    }

    suspend fun getUndoneTasks(): List<TodoItem> {
        val taskList = mutableListOf<TodoItem>()
        for (item in todoItems) {
            if (!item.value.status) {
                taskList.add(item.value)
            }
        }
        return taskList
    }

    suspend fun checkItem(id: String, status: Boolean) {
        val newItem = todoItems[id]?.copy(status = status)
        if (newItem != null) todoItems[id] = newItem
    }

    suspend fun addTodoItem(todoItem: TodoItem) {
        todoItems[todoItem.id] = todoItem
    }

    suspend fun deleteItem(todoItemId: String) {
        todoItems.remove(todoItemId)
    }

    suspend fun getItem(todoItemId: String) : TodoItem? {
        return todoItems[todoItemId]
    }

    suspend fun generateId() : String {
        return UUID.randomUUID().toString()
    }

    fun getCurrentRevision(): Int {
        return currentRevision
    }
}
