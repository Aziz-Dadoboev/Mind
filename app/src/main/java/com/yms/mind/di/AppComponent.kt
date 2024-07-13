package com.yms.mind.di

import android.app.Application
import android.content.Context
import com.yms.mind.MindApp
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
@Component( modules = [AppModule::class, NetworkModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(application: MindApp)
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
        applicationScope: CoroutineScope
    ): TodoItemsRepository {
        return TodoItemsRepository(
            todoApiService,
            applicationScope,
            Dispatchers.IO
        )
    }
}