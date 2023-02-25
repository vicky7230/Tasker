package com.vicky7230.tasker.ui.home

import dagger.Module
import dagger.Provides

@Module
class HomeModule {

    @Provides
    fun provideTaskListsAdapter(): TaskListsAdapter {
        return TaskListsAdapter(arrayListOf())
    }
    @Provides
    fun provideTodayTaskAdapter(): TodayTaskAdapter {
        return TodayTaskAdapter(arrayListOf())
    }
}