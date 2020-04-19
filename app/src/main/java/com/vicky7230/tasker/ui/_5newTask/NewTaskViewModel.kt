package com.vicky7230.tasker.ui._5newTask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewTaskViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    val taskList = MutableLiveData<List<TaskList>>()
    var taskInserted = MutableLiveData<Long>()
    var task = MutableLiveData<Task>()

    fun getData(taskLongId: Long) {
        viewModelScope.launch {
            if (taskLongId != -1L)
                task.value = dataManager.getTask(taskLongId)
            taskList.value = dataManager.getLists()
        }
    }

    fun saveTaskInDB(task: Task) {
        viewModelScope.launch {
            val taskId = dataManager.insertTask(task)
            taskInserted.value = taskId
        }
    }
}