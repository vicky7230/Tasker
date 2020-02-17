package com.vicky7230.tasker.data.network

import com.google.gson.JsonElement
import retrofit2.Response
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
}