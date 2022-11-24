package com.vicky7230.tasker.data

import com.vicky7230.tasker.data.db.AppDbHelper
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import com.vicky7230.tasker.data.network.AppApiHelper
import com.vicky7230.tasker.data.prefs.AppPreferencesHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppDataManager @Inject
constructor(
    private val appApiHelper: AppApiHelper,
    private val appDbHelper: AppDbHelper,
    private val appPreferencesHelper: AppPreferencesHelper
) : DataManager {

    override fun getAccessToken(): String? {
        return appPreferencesHelper.getAccessToken()
    }

    override fun setAccessToken(accessToken: String) {
        appPreferencesHelper.setAccessToken(accessToken)
    }

    override fun getUserEmail(): String? {
        return appPreferencesHelper.getUserEmail()
    }

    override fun setUserEmail(email: String) {
        appPreferencesHelper.setUserEmail(email)
    }

    override fun getUserId(): String? {
        return appPreferencesHelper.getUserId()
    }

    override fun setUserId(userId: String) {
        appPreferencesHelper.setUserId(userId)
    }

    override fun getUserLoggedIn(): Boolean {
        return appPreferencesHelper.getUserLoggedIn()
    }

    override fun setUserLoggedIn() {
        appPreferencesHelper.setUserLoggedIn()
    }

    override fun setUserLoggedOut() {
        appPreferencesHelper.setUserLoggedOut()
    }


    override fun getAllLists(): Flow<List<TaskList>> {
        return appDbHelper.getAllLists()
    }

    override suspend fun getLists(): List<TaskList> {
        return appDbHelper.getLists()
    }

    override fun getAllListsWithTaskCount(): Flow<List<TaskListAndCount>> {
        return appDbHelper.getAllListsWithTaskCount()
    }

    override suspend fun insertTaskLists(taskLists: List<TaskList>): List<Long> {
        return appDbHelper.insertTaskLists(taskLists)
    }

    override suspend fun insertTask(task: Task): Long {
        return appDbHelper.insertTask(task)
    }

    override suspend fun getTask(taskLongId: Long): Task {
        return appDbHelper.getTask(taskLongId)
    }

    override fun getTasksForToday(
        todaysDateStart: Long,
        todaysDateEnd: Long
    ): Flow<List<TaskAndTaskList>> {
        return appDbHelper.getTasksForToday(todaysDateStart, todaysDateEnd)
    }

    override suspend fun updateTask(task: Task): Int {
        return appDbHelper.updateTask(task)
    }

    override suspend fun insertTasks(tasksAndListFromServer: MutableList<Task>): List<Long> {
        return appDbHelper.insertTasks(tasksAndListFromServer)
    }

    override fun getTasksForList(listId: Long): Flow<List<Task>> {
        return appDbHelper.getTasksForList(listId)
    }

    override suspend fun setTaskFinished(id: Long): Int {
        return appDbHelper.setTaskFinished(id)
    }

    override suspend fun setTaskDeleted(id: Long): Int {
        return appDbHelper.setTaskDeleted(id)
    }

    override suspend fun updateTaskList(name: String, listId: Long): Int {
        return appDbHelper.updateTaskList(name, listId)
    }

    override suspend fun getDeletedTasks(): List<TaskAndTaskList> {
        return appDbHelper.getDeletedTasks()
    }

    override suspend fun getFinishedTasks(): List<TaskAndTaskList> {
        return appDbHelper.getFinishedTasks()
    }

    override suspend fun setListDeleted(id: Long): Int {
        return appDbHelper.setListDeleted(id)
    }

    override suspend fun deleteListAndTasks(lisId: Long): Int {
        return appDbHelper.deleteListAndTasks(lisId)
    }

    override fun areListsFetched(): Boolean {
        return appPreferencesHelper.areListsFetched()
    }

    override fun setListsFetched() {
        appPreferencesHelper.setListsFetched()
    }

    override fun areTasksFetched(): Boolean {
        return appPreferencesHelper.areTasksFetched()
    }

    override fun setTasksFetched() {
        appPreferencesHelper.setTasksFetched()
    }

}