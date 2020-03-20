package com.vicky7230.tasker.data

import com.google.gson.JsonElement
import com.vicky7230.tasker.data.db.AppDbHelper
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import com.vicky7230.tasker.data.network.AppApiHelper
import com.vicky7230.tasker.data.prefs.AppPreferencesHelper
import com.vicky7230.tasker.worker.TaskSync
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class AppDataManager @Inject
constructor(
    private val appApiHelper: AppApiHelper,
    private val appDbHelper: AppDbHelper,
    private val appPreferencesHelper: AppPreferencesHelper
) : DataManager {

    override suspend fun generateOtp(email: String): Response<JsonElement> {
        return appApiHelper.generateOtp(email)
    }

    override suspend fun verifyOtp(email: String, otp: String): Response<JsonElement> {
        return appApiHelper.verifyOtp(email, otp)
    }

    override suspend fun getUserTaskLists(userId: String?, token: String?): Response<JsonElement> {
        return appApiHelper.getUserTaskLists(userId, token)
    }

    override suspend fun syncSingleTask(taskSync: TaskSync): Response<JsonElement> {
        return appApiHelper.syncSingleTask(taskSync)
    }

    override suspend fun getUserTasks(userId: String?, token: String?): Response<JsonElement> {
        return appApiHelper.getUserTasks(userId, token)
    }

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


    override fun getAllLists(): Flow<List<TaskList>> {
        return appDbHelper.getAllLists()
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