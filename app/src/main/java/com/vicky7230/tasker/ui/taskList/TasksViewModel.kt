package com.vicky7230.tasker.ui.taskList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val dataManager: DataManager) : BaseViewModel() {

    val tasks = MutableLiveData<List<Task>>()
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