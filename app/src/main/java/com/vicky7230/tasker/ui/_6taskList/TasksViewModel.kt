package com.vicky7230.tasker.ui._6taskList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.events.TokenExpireEvent
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel(dataManager) {

    val tasks = MutableLiveData<List<Task>>()
    var taskFinished = MutableLiveData<Long>()
    var taskDeleted = MutableLiveData<Long>()
    var listDeleted = MutableLiveData<Boolean>()
    var listRenamed = MutableLiveData<String>()

    fun getTasks(listId: Long) {
        viewModelScope.launch {
            dataManager.getTasksForList(listId)
                .collect { tasksForList: List<Task> ->
                    tasks.value = tasksForList
                }
        }
    }

    fun deleteTasK(task: Task) {
        viewModelScope.launch {
            if (task.deleted != 1) {
                val count = dataManager.setTaskDeleted(task.id)
                if (count > 0)
                    taskDeleted.value = task.id
            }
        }
    }

    fun setTaskFinished(task: Task) {
        viewModelScope.launch {
            if (task.deleted != 1) {
                val count = dataManager.setTaskDeleted(task.id)
                if (count > 0)
                    taskDeleted.value = task.id
            }
        }
    }

    fun updateTaskList(listId: Long, listName: String) {
        viewModelScope.launch {
            if (dataManager.updateTaskList(listName, listId) > 0)
                listRenamed.value = listName
        }
    }

    fun deleteTaskList(listId: Long) {
        viewModelScope.launch {
            val count = dataManager.deleteListAndTasks(listId)
            if (count > 0)
                listDeleted.value = true
        }
    }
}