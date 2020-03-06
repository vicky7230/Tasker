package com.vicky7230.tasker.ui._0base

import androidx.lifecycle.ViewModel
import com.vicky7230.tasker.data.network.Resource
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import timber.log.Timber

open class BaseViewModel : ViewModel() {

    suspend fun <T : Any> safeApiCall(
        call: suspend () -> Response<T>
    ): Resource<T>? {
        try {
            val response = call.invoke()
            if (response.isSuccessful) {
                /** Called for [200, 300) responses. */
                return Resource.Success(response.body()!!)
            } else {
                return when (response.code()) {
                    401 -> {
                        Resource.Error("HTTP ${response.code()} : Unauthorized")
                    }
                    in 400..499 -> {
                        Resource.Error("HTTP ${response.code()} : Client Error")
                    }
                    in 500..599 -> {
                        Resource.Error("HTTP ${response.code()} : Server Error")
                    }
                    else -> {
                        Resource.Error("HTTP ${response.code()} : Something went wrong")
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                Timber.d("Job was Cancelled....")
            }
            //Log exception
            Timber.e(e)
            return e.localizedMessage?.let { Resource.Error(it) }
        }
    }
}