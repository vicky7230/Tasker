package com.vicky7230.tasker.data.network

/*
sealed class RetrofitResult<out T: Any> {
    data class Success<out T : Any>(val data: T) : RetrofitResult<T>()
    data class Error(val exception: Exception) : RetrofitResult<Nothing>()
}
*/

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}