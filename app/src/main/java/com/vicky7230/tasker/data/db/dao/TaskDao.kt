package com.vicky7230.tasker.data.db.dao

import androidx.room.*
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskAndTaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Query("SELECT * FROM tasks WHERE id =:taskLongId")
    suspend fun getTask(taskLongId: Long): Task

    @Query("SELECT * FROM tasks LEFT JOIN lists ON tasks.list_slack = lists.list_slack WHERE date_time>= :dateTime")
    fun getTasksForToday(dateTime: Long): Flow<List<TaskAndTaskList>>

    @Update
    suspend fun updateTask(task: Task): Int
}