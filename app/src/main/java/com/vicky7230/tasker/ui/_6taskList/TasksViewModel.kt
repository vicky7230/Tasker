package com.vicky7230.tasker.ui._6taskList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    val tasks = MutableLiveData<List<Task>>()

    fun getTasks(listSlack: String) {
        viewModelScope.launch {
            tasks.value = dataManager.getTasksForList(listSlack)
        }
    }

}