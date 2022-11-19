package com.vicky7230.tasker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vicky7230.tasker.data.db.dao.TaskDao
import com.vicky7230.tasker.data.db.dao.TaskListDao
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskList

@Database(entities = [Task::class, TaskList::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun tasklistDao(): TaskListDao

    // Function to create object of Room Database
    companion object {
        private var INSTANCE: AppDatabase? = null
        val MIGRATION_1_2: Migration =
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE lists ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0")
                }
            }

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app.db"
                    )
                        .addCallback(rdc)
                        //.addMigrations(MIGRATION_1_2)
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

var rdc: RoomDatabase.Callback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        // do something after database has been created
        db.execSQL("INSERT INTO lists (name, color, deleted) VALUES('Work','#61DEA4',0)")
        db.execSQL("INSERT INTO lists (name, color, deleted) VALUES('Shopping','#F45E6D',0)")
        db.execSQL("INSERT INTO lists (name, color, deleted) VALUES('Family','#FFE761',0)")
        db.execSQL("INSERT INTO lists (name, color, deleted) VALUES('Personal','#B678FF',0)")
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        // do something every time database is open
    }
}