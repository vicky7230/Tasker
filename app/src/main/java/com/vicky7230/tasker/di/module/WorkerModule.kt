package com.vicky7230.tasker.di.module

import com.vicky7230.tasker.di.ChildWorkerFactory
import com.vicky7230.tasker.di.WorkerKey
import com.vicky7230.tasker.worker.CreateTaskWorker
import com.vicky7230.tasker.worker.UpdateTaskWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(CreateTaskWorker::class)
    internal abstract fun bindCreateTaskWorker(createTaskWorker: CreateTaskWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(UpdateTaskWorker::class)
    internal abstract fun bindUpdateTaskWorker(updateTaskWorker: UpdateTaskWorker.Factory): ChildWorkerFactory
}