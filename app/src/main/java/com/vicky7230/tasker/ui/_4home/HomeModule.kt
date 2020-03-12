package com.vicky7230.tasker.ui._4home

import dagger.Module
import dagger.Provides

@Module
class HomeModule {

    @Provides
    fun provideTaskListsAdapter(): TaskListsAdapter {
        return TaskListsAdapter(arrayListOf())
    }
    @Provides
    fun provideTodaysTaskAdapter(): TodaysTaskAdapter {
        return TodaysTaskAdapter(arrayListOf())
    }
}