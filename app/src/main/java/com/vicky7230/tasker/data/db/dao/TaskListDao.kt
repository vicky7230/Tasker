package com.vicky7230.tasker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vicky7230.tasker.data.db.entities.TaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {

    @Query("SELECT * FROM lists")
    fun getAllLists(): Flow<List<TaskList>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLists(vararg taskLists: TaskList): List<Long>
}