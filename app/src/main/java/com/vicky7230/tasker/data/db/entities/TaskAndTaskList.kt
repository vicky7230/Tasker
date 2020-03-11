package com.vicky7230.tasker.data.db.entities

import androidx.room.Embedded

data class TaskAndTaskList(
    @Embedded
    var task: Task,
    @Embedded
    var taskList: TaskList
)