package com.vicky7230.tasker.ui._4home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.network.RetrofitResult
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    var loading = MutableLiveData<Boolean>()
    var error = MutableLiveData<String>()

    private val _taskList = MutableLiveData<List<TaskList>>()
    val taskList: LiveData<List<TaskList>> = _taskList

    fun getAllList() {
        viewModelScope.launch {
            dataManager.getAllLists().collect { taskListsFromDb: List<TaskList> ->
                if (taskListsFromDb.isNotEmpty())
                    _taskList.value = taskListsFromDb
                else {

                    loading.value = true

                    val response = safeApiCall {
                        dataManager.getUserTaskLists(
                            dataManager.getUserId(),
                            dataManager.getAccessToken()
                        )
                    }

                    when (response) {
                        is RetrofitResult.Success -> {
                            val jsonObject = response.data.asJsonObject
                            val taskListsJsonArray = jsonObject["task_lists"].asJsonArray
                            val taskListsFromNetwork: MutableList<TaskList> = arrayListOf()
                            taskListsJsonArray.forEach { taskListJsonElement: JsonElement ->
                                taskListsFromNetwork.add(
                                    TaskList(
                                        taskListJsonElement.asJsonObject["list_slack"].asString,
                                        taskListJsonElement.asJsonObject["name"].asString,
                                        taskListJsonElement.asJsonObject["color"].asString
                                    )
                                )
                            }
                            dataManager.insertTaskLists(taskListsFromNetwork)
                        }
                        is RetrofitResult.Error -> {
                            error.value = response.exception.message
                        }
                    }

                    loading.value = false
                }
            }
            //_taskList.value = dataManager.getAllLists().asLiveData().value
        }
    }


}
