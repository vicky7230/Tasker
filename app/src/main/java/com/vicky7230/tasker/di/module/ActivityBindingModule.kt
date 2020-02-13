package com.vicky7230.tasker.di.module

import com.vicky7230.tasker.ui.home.HomeActivity
import com.vicky7230.tasker.ui.home.HomeModule
import com.vicky7230.tasker.ui.newTask.NewTaskActivity
import com.vicky7230.tasker.ui.newTask.NewTaskModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by vicky on 1/1/18.
 */
@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [(HomeModule::class)])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [(NewTaskModule::class)])
    abstract fun bindNewTaskActivity(): NewTaskActivity

    /*@ContributesAndroidInjector(modules = [(SavedCitiesModule::class)])
    abstract fun bindSavedCitiesActivity(): SavedCitiesActivity*/

}