package com.vicky7230.tasker.data.db.entities

import androidx.room.ColumnInfo

data class TaskListAndCount(
    @ColumnInfo(name = "id")
    var id: Long,
    @ColumnInfo(name = "list_slack")//this is the server identifier of this tasklist
    var listSlack: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "color")
    var color: String,
    @ColumnInfo(name = "task_count")
    var taskCount: Int
)