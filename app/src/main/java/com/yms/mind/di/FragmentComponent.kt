package com.yms.mind.di

import com.yms.mind.data.repository.TodoItemsRepository
import com.yms.mind.ui.fragments.EditTodoItemFragment
import com.yms.mind.ui.fragments.SettingsFragment
import com.yms.mind.ui.fragments.TodoItemsFragment
import com.yms.mind.viewmodels.ViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.Subcomponent


@FragmentScope
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
    fun inject(fragment: TodoItemsFragment)
    fun inject(fragment: EditTodoItemFragment)
    fun inject(fragment: SettingsFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): FragmentComponent
    }
}

@Module
class FragmentModule {
    @Provides
    @FragmentScope
    fun provideTodoViewModelFactory(
        repository: TodoItemsRepository
    ): ViewModelFactory {
        return ViewModelFactory(repository)
    }
}