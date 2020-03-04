package com.vicky7230.tasker.data.db.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Long,

    @ColumnInfo(name = "task_slack")//this is the server identifier of this task
    var serverPrimaryKey: String,

    @ColumnInfo(name = "task")
    var task: String,

    @ColumnInfo(name = "date_time")
    var dateTime: Long,

    @ColumnInfo(name = "list_slack")
    var listSlack: String
)