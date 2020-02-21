package com.vicky7230.tasker.data.db

import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.entities.Task
import kotlinx.coroutines.flow.Flow


interface DbHelper {

    fun getAllLists(): Flow<List<TaskList>>

    suspend fun insertTaskLists(taskLists: List<TaskList>): List<Long>

    suspend fun insertTodo(task: Task)
}
