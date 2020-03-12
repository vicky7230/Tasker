package com.vicky7230.tasker.data.network

import com.google.gson.JsonElement
import com.vicky7230.tasker.worker.TaskSync
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {

    @FormUrlEncoded
    @POST("api/LoginApi/generateOTP")
    suspend fun generateOtp(
        @Field("email") email: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST("api/LoginApi/verifyOTP")
    suspend fun verifyOtp(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST("api/TaskApi/getUserTaskLists")
    suspend fun getUserTaskLists(
        @Field("userID") userId: String?,
        @Field("token") token: String?
    ): Response<JsonElement>

    @POST("api/TaskApi/syncSingleTask")
    suspend fun syncSingleTask(
        @Body taskSync: TaskSync
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST("api/TaskApi/getUserTasks")
    suspend fun getUserTasks(
        @Field("userID") userId: String?,
        @Field("token") token: String?
    ): Response<JsonElement>

}