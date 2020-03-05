package com.vicky7230.tasker.data.network

import com.google.gson.JsonElement
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.worker.TaskSync
import retrofit2.Response


interface ApiHelper {

    suspend fun generateOtp(email: String): Response<JsonElement>

    suspend fun verifyOtp(email: String, otp: String): Response<JsonElement>

    suspend fun getUserTaskLists(userId: String?, token: String?): Response<JsonElement>

    suspend fun syncSingleTask(taskSync: TaskSync): Response<JsonElement>
}