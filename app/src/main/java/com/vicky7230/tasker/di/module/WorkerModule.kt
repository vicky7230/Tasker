package com.vicky7230.tasker.di.module

import com.vicky7230.tasker.di.ChildWorkerFactory
import com.vicky7230.tasker.di.WorkerKey
import com.vicky7230.tasker.worker.TaskSyncWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(TaskSyncWorker::class)
    internal abstract fun bindTaskSyncWorker(taskSyncWorker: TaskSyncWorker.Factory): ChildWorkerFactory
}