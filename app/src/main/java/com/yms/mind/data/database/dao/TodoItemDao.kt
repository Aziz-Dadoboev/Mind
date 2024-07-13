package com.yms.mind.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yms.mind.data.database.entities.TodoItemEntity

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_items")
    fun getTodoItems(): List<TodoItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItemEntity)

    @Update
    suspend fun updateTodoItem(todoItem: TodoItemEntity)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItems(todoItems: List<TodoItemEntity>)

    @Query("DELETE FROM todo_items")
    suspend fun deleteAll()

}