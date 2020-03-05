package com.vicky7230.tasker.ui._5newTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewTaskViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    private val _taskList = MutableLiveData<List<TaskList>>()
    val taskList: LiveData<List<TaskList>> = _taskList
    var taskInserted  = MutableLiveData<Long>()

    fun getAllList() {
        viewModelScope.launch {
            dataManager.getAllLists().collect { taskListsFromDb: List<TaskList> ->
                if (taskListsFromDb.isNotEmpty())
                    _taskList.value = taskListsFromDb
            }
        }
    }

    fun saveTaskInDB(task: Task) {
        viewModelScope.launch {
            val taskId = dataManager.insertTask(task)
            taskInserted.value = taskId
        }
    }
}