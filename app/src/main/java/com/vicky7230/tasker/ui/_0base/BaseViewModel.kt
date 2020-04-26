package com.vicky7230.tasker.ui._0base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.network.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

open class BaseViewModel(private var dataManager: DataManager) : ViewModel() {

    var loggedOut = MutableLiveData<Boolean>()

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
                        Resource.Error(IOException("HTTP ${response.code()} : Unauthorized"))
                    }
                    in 400..499 -> {
                        Resource.Error(IOException("HTTP ${response.code()} : Client Error"))
                    }
                    in 500..599 -> {
                        Resource.Error(IOException("HTTP ${response.code()} : Server Error"))
                    }
                    else -> {
                        Resource.Error(IOException("HTTP ${response.code()} : Something went wrong"))
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                Timber.d("Job was Cancelled....")
            }
            //Log exception
            Timber.e(e)
            return Resource.Error(e)
        }
    }

    fun onTokenExpire() {
        viewModelScope.launch {
            dataManager.setUserLoggedOut()
            loggedOut.value = true
        }
    }

}