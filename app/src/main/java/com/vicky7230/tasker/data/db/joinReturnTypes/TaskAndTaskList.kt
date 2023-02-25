package com.vicky7230.tasker.data.db.joinReturnTypes

import androidx.room.ColumnInfo

data class TaskAndTaskList(
    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "list_id")//local unique identifier
    var listId: Long,

    @ColumnInfo(name = "task")
    var task: String,

    @ColumnInfo(name = "date_time")
    var dateTime: Long,

    @ColumnInfo(name = "finished")
    var finished: Int,

    @ColumnInfo(name = "deleted")
    var deleted: Int,

    @ColumnInfo(name = "list_name")
    var listName: String,

    @ColumnInfo(name = "list_color")
    var listColor: String
)