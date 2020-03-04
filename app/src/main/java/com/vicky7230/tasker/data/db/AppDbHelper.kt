package com.vicky7230.tasker.data.db


import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.entities.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppDbHelper @Inject constructor(private val appDatabase: AppDatabase) : DbHelper {

    override fun getAllLists(): Flow<List<TaskList>> {
        return appDatabase.tasklistDao().getAllLists()
    }

    override suspend fun insertTaskLists(taskLists: List<TaskList>): List<Long> {
        return appDatabase.tasklistDao().insertTaskLists(taskLists)
    }

    override suspend fun insertTask(task: Task) : Long {
        return  appDatabase.todoDao().insertTask(task)
    }


}