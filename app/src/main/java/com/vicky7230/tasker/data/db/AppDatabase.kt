package com.vicky7230.tasker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vicky7230.tasker.data.db.dao.TaskListDao
import com.vicky7230.tasker.data.db.dao.TaskDao
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskList

@Database(entities = [Task::class, TaskList::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun tasklistDao(): TaskListDao

    // Function to create object of Room Database
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "app.db"
                    )
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}