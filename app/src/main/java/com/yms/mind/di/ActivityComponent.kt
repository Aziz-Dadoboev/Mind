package com.yms.mind.di

import com.yms.mind.activities.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface ActivityComponent {
    fun inject(activity: MainActivity)
    fun fragmentComponentFactory(): FragmentComponent.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: MainActivity): ActivityComponent
    }
}