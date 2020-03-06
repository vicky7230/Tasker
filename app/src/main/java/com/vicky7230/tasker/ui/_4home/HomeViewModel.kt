package com.vicky7230.tasker.ui._4home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    var resource = MutableLiveData<Resource<List<TaskList>>>()

    fun getAllList() {
        viewModelScope.launch {
            dataManager.getAllLists().collect { taskListsFromDb: List<TaskList> ->
                if (taskListsFromDb.isNotEmpty())
                    resource.value = Resource.Success(taskListsFromDb)
                else {

                    resource.value = Resource.Loading

                    val response = safeApiCall {
                        dataManager.getUserTaskLists(
                            dataManager.getUserId(),
                            dataManager.getAccessToken()
                        )
                    }

                    when (response) {
                        is Resource.Success -> {
                            val jsonObject = response.data.asJsonObject
                            if (jsonObject.get("success").asBoolean) {
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
                                resource.value = Resource.Success(taskListsFromNetwork)
                            } else {
                                resource.value =
                                    Resource.Error(IOException(jsonObject.get("message").asString))
                            }
                        }
                        is Resource.Error -> {
                            resource.value = response
                        }
                    }
                }
            }
        }
    }


}
