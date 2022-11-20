package com.vicky7230.tasker.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.di.ViewModelFactory
import com.vicky7230.tasker.di.ViewModelKey
import com.vicky7230.tasker.ui._1splash.SplashViewModel
import com.vicky7230.tasker.ui._4home.HomeViewModel
import com.vicky7230.tasker.ui._5newTask.NewTaskViewModel
import com.vicky7230.tasker.ui._6taskList.TasksViewModel
import com.vicky7230.tasker.ui._7finishedDeleted.FinishedDeletedTasksViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun postSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun postHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewTaskViewModel::class)
    internal abstract fun postNewTaskViewModel(newTaskViewModel: NewTaskViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TasksViewModel::class)
    internal abstract fun postTasksViewModel(tasksViewModel: TasksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FinishedDeletedTasksViewModel::class)
    internal abstract fun postFinishedDeletedTasksViewModel(finishedDeletedTasksViewModel: FinishedDeletedTasksViewModel): ViewModel

}