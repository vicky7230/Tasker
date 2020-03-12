package com.vicky7230.tasker.data.network


import com.google.gson.JsonElement
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.worker.TaskSync
import retrofit2.Response
import javax.inject.Inject

class AppApiHelper @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun generateOtp(email: String): Response<JsonElement> {
        return apiService.generateOtp(email)
    }

    override suspend fun verifyOtp(email: String, otp: String): Response<JsonElement> {
        return apiService.verifyOtp(email, otp)
    }

    override suspend fun getUserTaskLists(userId: String?, token: String?): Response<JsonElement> {
        return apiService.getUserTaskLists(userId, token)
    }

    override suspend fun syncSingleTask(taskSync: TaskSync): Response<JsonElement> {
        return apiService.syncSingleTask(taskSync)
    }

    override suspend fun getUserTasks(userId: String?, token: String?): Response<JsonElement> {
        return apiService.getUserTasks(userId, token)
    }

}