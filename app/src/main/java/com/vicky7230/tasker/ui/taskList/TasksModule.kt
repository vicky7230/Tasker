package com.vicky7230.tasker.ui.taskList

import dagger.Module
import dagger.Provides

@Module
class TasksModule {

    @Provides
    fun provideTasksForListAdapter(): TasksForListAdapter {
        return TasksForListAdapter(arrayListOf())
    }
}