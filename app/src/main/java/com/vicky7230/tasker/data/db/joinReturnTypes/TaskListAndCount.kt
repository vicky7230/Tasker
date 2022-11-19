package com.vicky7230.tasker.data.db.joinReturnTypes

import androidx.room.ColumnInfo

data class TaskListAndCount(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "color")
    var color: String,

    @ColumnInfo(name = "task_count")// no. of tasks present in this task_list
    var taskCount: Int
)