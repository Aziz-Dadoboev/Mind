package com.yms.mind.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yms.mind.data.database.dao.TodoItemDao
import com.yms.mind.data.database.entities.TodoItemEntity

@Database(entities = [TodoItemEntity::class], version = 1)
@TypeConverters(Converter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract val dao: TodoItemDao
}