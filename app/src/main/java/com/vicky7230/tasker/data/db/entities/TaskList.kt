package com.vicky7230.tasker.data.db.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "lists")
data class TaskList(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Long,

    @ColumnInfo(name = "list_slack")//this is the server identifier of this tasklist
    var listSlack: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "color")
    var color: String,

    @ColumnInfo(name = "deleted")
    var deleted: Int = 0
)