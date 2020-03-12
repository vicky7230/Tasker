package com.vicky7230.tasker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {

    @Query("SELECT * FROM lists")
    fun getAllLists(): Flow<List<TaskList>>

    @Query(
        "SELECT COUNT(tasks.task_slack) as task_count, lists.id, lists.list_slack, lists.name, lists.color FROM lists LEFT JOIN tasks ON lists.list_slack = tasks.list_slack GROUP BY lists.list_slack"
    )
    fun getAllListsWithTaskCount(): Flow<List<TaskListAndCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLists(taskLists: List<TaskList>): List<Long>
}