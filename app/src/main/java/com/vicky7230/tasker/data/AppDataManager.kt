package com.vicky7230.tasker.data

import com.google.gson.JsonElement
import com.vicky7230.tasker.data.db.AppDbHelper
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.network.AppApiHelper
import com.vicky7230.tasker.data.prefs.AppPreferencesHelper
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

    override suspend fun insertTodo(task: Task) {
        appDbHelper.insertTodo(task)
    }

}