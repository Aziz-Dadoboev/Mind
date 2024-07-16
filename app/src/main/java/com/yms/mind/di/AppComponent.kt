package com.yms.mind.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.yms.mind.data.database.TodoDatabase
import com.yms.mind.data.database.dao.TodoItemDao
import com.yms.mind.data.network.ApiService
import com.yms.mind.data.network.RetrofitInstance
import com.yms.mind.data.repository.TodoItemsRepository
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class, DatabaseModule::class, SharedPreferencesModule::class])
interface AppComponent {
    fun activityComponentFactory(): ActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}


@Module
object AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
}

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideTodoApiService(): ApiService = RetrofitInstance.api
}

@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideTodoItemsRepository(
        todoApiService: ApiService,
        todoItemDao: TodoItemDao,
        applicationScope: CoroutineScope,
        sharedPreferences: SharedPreferences
    ): TodoItemsRepository {
        return TodoItemsRepository(
            todoApiService,
            applicationScope,
            Dispatchers.IO,
            todoItemDao,
            sharedPreferences,
        )
    }
}

@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = TodoDatabase::class.java,
            name = "todo_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoItemDao(database: TodoDatabase): TodoItemDao {
        return database.dao
    }
}

@Module
object SharedPreferencesModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)
    }
}