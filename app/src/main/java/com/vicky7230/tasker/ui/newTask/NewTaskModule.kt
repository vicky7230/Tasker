package com.vicky7230.tasker.ui.newTask

import dagger.Module
import dagger.Provides

@Module
class NewTaskModule {

    @Provides
    fun provideTaskListsAdapter2(): TaskListsAdapter2 {
        return TaskListsAdapter2(arrayListOf())
    }
}