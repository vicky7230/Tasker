package com.vicky7230.tasker.ui.finishedDeleted

import dagger.Module
import dagger.Provides

@Module
class FinishedDeletedTasksModule {

    @Provides
    fun provideDeletedFinishedTasksAdapter(): DeletedFinishedTasksAdapter {
        return DeletedFinishedTasksAdapter(arrayListOf())
    }
}