package com.vicky7230.tasker.ui._7finishedDeleted

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class FinishedDeletedTasksViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel(dataManager) {

    var tasks = MutableLiveData<List<TaskAndTaskList>>()

    fun getDeletedTasks() {
        viewModelScope.launch {
            tasks.value = dataManager.getDeletedTasks()
        }
    }

    fun getFinishedTasks() {
        viewModelScope.launch {
            tasks.value = dataManager.getFinishedTasks()
        }
    }

}