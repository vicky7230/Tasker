package com.vicky7230.tasker.worker

import com.vicky7230.tasker.data.db.entities.Task

data class TaskData(
    val userID: String?,
    val token: String?,
    val task: Task
)