package com.vicky7230.tasker.data.db.dao

import androidx.room.*
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Query("SELECT * FROM tasks WHERE id =:taskLongId")
    suspend fun getTask(taskLongId: Long): Task

    @Query("SELECT tasks.id, tasks.task_slack, tasks.list_slack, tasks.task, tasks.date_time, lists.name, lists.color FROM tasks LEFT JOIN lists ON tasks.list_slack = lists.list_slack WHERE date_time>= :dateTime")
    fun getTasksForToday(dateTime: Long): Flow<List<TaskAndTaskList>>

    @Update
    suspend fun updateTask(task: Task): Int
}