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
    var listRenamed = MutableLiveData<Resource<String>>()

    fun getTasks(listSlack: String) {
        viewModelScope.launch {
            dataManager.getTasksForList(listSlack)
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

    fun renameTaskList(listName: String, listSlack: String) {
        viewModelScope.launch {
            listRenamed.value = Resource.Loading

            val response = safeApiCall {
                dataManager.renameList(
                    dataManager.getUserId(),
                    dataManager.getAccessToken(),
                    listName,
                    listSlack
                )
            }

            when (response) {
                is Resource.Success -> {
                    val jsonObject = response.data.asJsonObject
                    if (jsonObject["success"].asBoolean) {
                        if (jsonObject["renamed"].asBoolean) {
                            updateTaskList(listSlack, listName)
                            listRenamed.value = Resource.Success(listName)
                        } else {
                            listRenamed.value =
                                Resource.Error(IOException(jsonObject["message"].asString))
                        }
                    } else {
                        listRenamed.value =
                            Resource.Error(IOException(jsonObject["message"].asString))
                        EventBus.getDefault().post(TokenExpireEvent())
                    }
                }

                is Resource.Error -> {
                    listRenamed.value = response
                }
            }
        }
    }

    private suspend fun updateTaskList(listSlack: String, listName: String) {
        dataManager.updateTaskList(listName, listSlack)
    }
}