package com.vicky7230.tasker.data.db


import com.vicky7230.tasker.data.db.entities.Task
import javax.inject.Inject

class AppDbHelper @Inject constructor(private val appDatabase: AppDatabase) : DbHelper {
    override suspend fun insertTodo(task: Task) {
        appDatabase.todoDao().insertTodo(task)
    }


}