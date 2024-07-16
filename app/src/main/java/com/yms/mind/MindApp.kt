package com.yms.mind

/*
TodoApp - класс для инициализации основных компонентов
приложения не связанных с жизненным циклом активити или фрагментов
 */


import android.app.Application
import com.yms.mind.di.AppComponent
import com.yms.mind.di.DaggerAppComponent

class MindApp : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }

//    private val todoApiService = RetrofitInstance.api
//    private val applicationScope = CoroutineScope(Dispatchers.Main)

//    val todoItemsRepository by lazy {
//        TodoItemsRepository(
//            todoApiService,
//            applicationScope,
//            Dispatchers.IO
//        )
//    }
}