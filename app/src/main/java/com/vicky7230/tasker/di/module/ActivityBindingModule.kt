package com.vicky7230.tasker.di.module

import com.vicky7230.tasker.ui.splash.SplashActivity
import com.vicky7230.tasker.ui.splash.SplashModule
import com.vicky7230.tasker.ui.home.HomeActivity
import com.vicky7230.tasker.ui.home.HomeModule
import com.vicky7230.tasker.ui.newTask.NewTaskActivity
import com.vicky7230.tasker.ui.newTask.NewTaskModule
import com.vicky7230.tasker.ui.taskList.TasksActivity
import com.vicky7230.tasker.ui.taskList.TasksModule
import com.vicky7230.tasker.ui.finishedDeleted.FinishedDeletedTasksActivity
import com.vicky7230.tasker.ui.finishedDeleted.FinishedDeletedTasksModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by vicky on 1/1/18.
 */
@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [(SplashModule::class)])
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [(HomeModule::class)])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [(NewTaskModule::class)])
    abstract fun bindNewTaskActivity(): NewTaskActivity

    @ContributesAndroidInjector(modules = [(TasksModule::class)])
    abstract fun bindTasksActivity(): TasksActivity

    @ContributesAndroidInjector(modules = [(FinishedDeletedTasksModule::class)])
    abstract fun bindFinishedDeletedTasksActivity(): FinishedDeletedTasksActivity

}