package com.vicky7230.tasker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.TaskList
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
            dataManager.getAllLists().collect {
                if (it.isNotEmpty())
                    _taskList.value = it
                else {

                    loading.value = true

                    val response = safeApiCall {
                        dataManager.getUserTaskLists(
                            dataManager.getUserId(),
                            dataManager.getAccessToken()
                        )
                    }

                    loading.value = false
                }
            }
            //_taskList.value = dataManager.getAllLists().asLiveData().value
        }
    }


}
