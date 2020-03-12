package com.vicky7230.tasker.data.db


import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppDbHelper @Inject constructor(private val appDatabase: AppDatabase) : DbHelper {

    override fun getAllLists(): Flow<List<TaskList>> {
        return appDatabase.tasklistDao().getAllLists()
    }

    override fun getAllListsWithTaskCount(): Flow<List<TaskListAndCount>> {
        return appDatabase.tasklistDao().getAllListsWithTaskCount()
    }

    override suspend fun insertTaskLists(taskLists: List<TaskList>): List<Long> {
        return appDatabase.tasklistDao().insertTaskLists(taskLists)
    }

    override suspend fun insertTask(task: Task) : Long {
        return  appDatabase.taskDao().insertTask(task)
    }

    override suspend fun getTask(taskLongId: Long): Task {
        return  appDatabase.taskDao().getTask(taskLongId)
    }

    override fun getTasksForToday(dateTime: Long): Flow<List<TaskAndTaskList>> {
        return appDatabase.taskDao().getTasksForToday(dateTime)
    }

    override suspend fun updateTask(task: Task): Int{
        return appDatabase.taskDao().updateTask(task)
    }

}