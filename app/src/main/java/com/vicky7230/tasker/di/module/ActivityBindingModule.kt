package com.vicky7230.tasker.di.module

import com.vicky7230.tasker.ui._1splash.SplashActivity
import com.vicky7230.tasker.ui._1splash.SplashModule
import com.vicky7230.tasker.ui._4home.HomeActivity
import com.vicky7230.tasker.ui._4home.HomeModule
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity
import com.vicky7230.tasker.ui._5newTask.NewTaskModule
import com.vicky7230.tasker.ui._6taskList.TasksActivity
import com.vicky7230.tasker.ui._6taskList.TasksModule
import com.vicky7230.tasker.ui._7finishedDeleted.FinishedDeletedTasksActivity
import com.vicky7230.tasker.ui._7finishedDeleted.FinishedDeletedTasksModule
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