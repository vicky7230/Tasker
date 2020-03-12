package com.vicky7230.tasker.data.db.joinReturnTypes

import androidx.room.ColumnInfo

data class TaskAndTaskList(
    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "task_id")//local unique identifier
    var taskId: String,

    @ColumnInfo(name = "task_slack")//this is the server identifier of this task
    var taskSlack: String,

    @ColumnInfo(name = "task")
    var task: String,

    @ColumnInfo(name = "date_time")
    var dateTime: Long,

    @ColumnInfo(name = "list_slack")
    var listSlack: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "color")
    var color: String
)