package com.vicky7230.tasker.data.db

import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskAndTaskList
import com.vicky7230.tasker.data.db.entities.TaskListAndCount
import kotlinx.coroutines.flow.Flow


interface DbHelper {

    fun getAllLists(): Flow<List<TaskList>>

    fun getAllListsWithTaskCount(): Flow<List<TaskListAndCount>>

    suspend fun insertTaskLists(taskLists: List<TaskList>): List<Long>

    suspend fun insertTask(task: Task): Long

    suspend fun getTask(taskLongId: Long): Task

    fun getTasksForToday(dateTime: Long): Flow<List<TaskAndTaskList>>

    suspend fun updateTask(task: Task): Int
}
