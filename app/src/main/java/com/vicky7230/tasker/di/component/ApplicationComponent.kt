package com.vicky7230.tasker.di.component

import com.vicky7230.tasker.TaskerApplication
import com.vicky7230.tasker.di.module.ActivityBindingModule
import com.vicky7230.tasker.di.module.ApplicationModule
import com.vicky7230.tasker.di.module.NetworkModule
import com.vicky7230.tasker.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by vicky on 12/2/18.
 */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        NetworkModule::class,
        ApplicationModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(taskerApplication: TaskerApplication): Builder

        fun build(): ApplicationComponent
    }

    fun inject(taskerApplication: TaskerApplication)
}