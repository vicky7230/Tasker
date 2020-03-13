package com.vicky7230.tasker.ui._4home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    var taskListAndCount = MutableLiveData<Resource<List<TaskListAndCount>>>()
    var taskAndTaskList = MutableLiveData<Resource<List<TaskAndTaskList>>>()

    fun getAllList() {
        viewModelScope.launch {
            dataManager.getAllListsWithTaskCount()
                .collect { taskListsFromDb: List<TaskListAndCount> ->
                    if (taskListsFromDb.isNotEmpty())
                        taskListAndCount.value = Resource.Success(taskListsFromDb)
                    else {

                        taskListAndCount.value = Resource.Loading

                        val response = safeApiCall {
                            dataManager.getUserTaskLists(
                                dataManager.getUserId(),
                                dataManager.getAccessToken()
                            )
                        }

                        when (response) {
                            is Resource.Success -> {
                                val jsonObject = response.data.asJsonObject
                                if (jsonObject["success"].asBoolean) {
                                    insertTaskListsInDb(jsonObject)
                                } else {
                                    taskListAndCount.value =
                                        Resource.Error(IOException(jsonObject.get("message").asString))
                                }
                            }
                            is Resource.Error -> {
                                taskListAndCount.value = response
                            }
                        }
                    }
                }
        }
    }

    private suspend fun insertTaskListsInDb(jsonObject: JsonObject) {
        val taskListsJsonArray = jsonObject["task_lists"].asJsonArray
        val taskListsFromNetwork: MutableList<TaskList> = arrayListOf()
        taskListsJsonArray.forEach { taskListJsonElement: JsonElement ->
            taskListsFromNetwork.add(
                TaskList(
                    0,
                    taskListJsonElement.asJsonObject["list_slack"].asString,
                    taskListJsonElement.asJsonObject["name"].asString,
                    taskListJsonElement.asJsonObject["color"].asString
                )
            )
        }
        dataManager.insertTaskLists(taskListsFromNetwork)
    }

    fun getTodaysTasks(todaysDateStart: Long, todaysDateEnd: Long) {
        viewModelScope.launch {
            dataManager.getTasksForToday(todaysDateStart, todaysDateEnd)
                .collect { tasksAndListFromDb: List<TaskAndTaskList> ->
                    if (tasksAndListFromDb.isNotEmpty())
                        taskAndTaskList.value = Resource.Success(tasksAndListFromDb)
                    else {
                        taskAndTaskList.value = Resource.Loading

                        val response = safeApiCall {
                            dataManager.getUserTasks(
                                dataManager.getUserId(),
                                dataManager.getAccessToken()
                            )
                        }

                        when (response) {
                            is Resource.Success -> {
                                val jsonObject = response.data.asJsonObject
                                if (jsonObject["success"].asBoolean) {
                                    insertTasksInDb(jsonObject)
                                } else {
                                    taskAndTaskList.value =
                                        Resource.Error(IOException(jsonObject["message"].asString))
                                }
                            }
                            is Resource.Error -> {
                                taskListAndCount.value = response
                            }
                        }
                    }
                }
        }
    }

    private suspend fun insertTasksInDb(jsonObject: JsonObject) {
        val tasksJsonArray = jsonObject["tasks"].asJsonArray
        val tasksAndListFromServer = mutableListOf<Task>()
        tasksJsonArray.forEach { taskJsonElement: JsonElement ->
            tasksAndListFromServer.add(
                Task(
                    0,
                    taskJsonElement.asJsonObject["user_task_id"].asString,
                    taskJsonElement.asJsonObject["task_slack"].asString,
                    taskJsonElement.asJsonObject["task"].asString,
                    taskJsonElement.asJsonObject["date_time"].asLong,
                    taskJsonElement.asJsonObject["list_slack"].asString
                )
            )
        }

        dataManager.insertTasks(tasksAndListFromServer)
    }


}
