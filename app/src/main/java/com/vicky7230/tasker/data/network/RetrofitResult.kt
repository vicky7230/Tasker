package com.vicky7230.tasker.data.network

sealed class RetrofitResult<out T: Any> {
    data class Success<out T : Any>(val data: T) : RetrofitResult<T>()
    data class Error(val exception: Exception) : RetrofitResult<Nothing>()
}