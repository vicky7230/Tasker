package com.vicky7230.tasker.data.db.dao

import androidx.room.*
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: MutableList<Task>): List<Long>

    @Query("SELECT * FROM tasks WHERE id =:taskLongId")
    suspend fun getTask(taskLongId: Long): Task

    @Query(
        """
        SELECT 
        tasks.id,tasks.list_id, tasks.task, tasks.date_time, tasks.finished, tasks.deleted, lists.name as list_name, lists.color as list_color 
        FROM tasks LEFT JOIN lists 
        ON tasks.list_id = lists.id 
        WHERE tasks.date_time >= :todaysDateStart 
        AND tasks.date_time <= :todaysDateEnd
        AND tasks.deleted != 1
        ORDER BY tasks.id DESC
        """
    )
    fun getTasksForToday(todaysDateStart: Long, todaysDateEnd: Long): Flow<List<TaskAndTaskList>>

    @Query(
        """
        SELECT 
        * FROM tasks 
        WHERE 
        id =:listID 
        AND finished != 1 
        AND deleted != 1 
        ORDER BY id DESC
        """
    )
    fun getTasksForList(listID: Long): Flow<List<Task>>

    @Update
    suspend fun updateTask(task: Task): Int

    @Query("UPDATE tasks SET finished = 1 WHERE id =:id")
    suspend fun setTaskFinished(id: Long): Int

    @Query("UPDATE tasks SET deleted = 1 WHERE id =:id")
    suspend fun setTaskDeleted(id: Long): Int

    @Query(
        """
        SELECT 
        tasks.id,tasks.list_id, tasks.task, tasks.date_time, tasks.finished, tasks.deleted, lists.name as list_name, lists.color as list_color 
        FROM tasks LEFT JOIN lists 
        ON tasks.list_id = lists.id
        WHERE tasks.deleted = 1
        ORDER BY tasks.id DESC
        """
    )
    suspend fun getDeletedTasks(): List<TaskAndTaskList>

    @Query(
        """
        SELECT 
        tasks.id,tasks.list_id, tasks.task, tasks.date_time, tasks.finished, tasks.deleted, lists.name as list_name, lists.color as list_color 
        FROM tasks LEFT JOIN lists 
        ON tasks.list_id = lists.id
        WHERE tasks.finished = 1
        ORDER BY tasks.id DESC
        """
    )
    suspend fun getFinishedTasks(): List<TaskAndTaskList>
}