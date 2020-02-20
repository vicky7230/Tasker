package com.vicky7230.tasker.data.network


import com.google.gson.JsonElement
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

}