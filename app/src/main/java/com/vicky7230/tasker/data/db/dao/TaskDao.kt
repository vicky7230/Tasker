package com.vicky7230.tasker.data.db.dao

import androidx.room.*
import com.vicky7230.tasker.data.db.entities.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Query("SELECT * FROM tasks WHERE id =:taskLongId")
    suspend fun getTask(taskLongId: Long): Task

    @Update
    suspend fun updateTask(task: Task): Int
}