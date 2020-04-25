package com.vicky7230.tasker.data.network

import com.google.gson.JsonElement
import com.vicky7230.tasker.worker.TaskData
import retrofit2.Response


interface ApiHelper {

    suspend fun generateOtp(email: String): Response<JsonElement>

    suspend fun verifyOtp(email: String, otp: String): Response<JsonElement>

    suspend fun refreshToken(userId: String?, token: String?): Response<JsonElement>

    suspend fun getUserTaskLists(userId: String?, token: String?): Response<JsonElement>

    suspend fun createTask(taskData: TaskData): Response<JsonElement>

    suspend fun updateTask(taskData: TaskData): Response<JsonElement>

    suspend fun getUserTasks(userId: String?, token: String?): Response<JsonElement>
}