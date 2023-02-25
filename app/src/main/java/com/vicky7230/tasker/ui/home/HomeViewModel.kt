package com.vicky7230.tasker.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val dataManager: DataManager) : BaseViewModel() {

    var taskListAndCount = MutableLiveData<Resource<List<TaskListAndCount>>>()
    var taskAndTaskList = MutableLiveData<Resource<List<TaskAndTaskList>>>()

    fun getData(todayDateStart: Long, todayDateEnd: Long) {

        viewModelScope.launch {
            dataManager.getTasksForToday(todayDateStart, todayDateEnd)
                .collect { tasksAndListFromDb: List<TaskAndTaskList> ->
                    if (tasksAndListFromDb.isNotEmpty())
                        taskAndTaskList.value = Resource.Success(tasksAndListFromDb)
                }
        }

        viewModelScope.launch {
            dataManager.getAllListsWithTaskCount()
                .collect { taskListsFromDb: List<TaskListAndCount> ->
                    if (taskListsFromDb.isNotEmpty())
                        taskListAndCount.value = Resource.Success(taskListsFromDb)
                }
        }
    }

    fun createNewList(listColor: String, listName: String) {
        viewModelScope.launch {
            insertTaskList(listName, listColor)
        }
    }

    private suspend fun insertTaskList(
        listName: String,
        listColor: String
    ) {
        val newTaskList: MutableList<TaskList> = arrayListOf()
        newTaskList.add(
            TaskList(
                0,
                listName,
                listColor
            )
        )
        dataManager.insertTaskLists(newTaskList)
    }
}
