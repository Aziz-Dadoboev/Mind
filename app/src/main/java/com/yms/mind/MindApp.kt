package com.yms.mind

/*
TodoApp - класс для инициализации основных компонентов
приложения не связанных с жизненным циклом активити или фрагментов
 */


import android.app.Application
import com.yms.mind.data.network.RetrofitInstance
import com.yms.mind.data.repository.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MindApp : Application() {
    private val todoApiService = RetrofitInstance.api
    private val applicationScope = CoroutineScope(Dispatchers.Main)
    val todoItemsRepository by lazy {
        TodoItemsRepository(
            todoApiService,
            applicationScope,
            Dispatchers.IO
        )
    }
}