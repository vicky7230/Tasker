package com.vicky7230.tasker.data.db

import com.vicky7230.tasker.data.db.entities.Task


interface DbHelper {
    suspend fun insertTodo(task: Task)
}
