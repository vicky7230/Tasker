package com.vicky7230.tasker.ui._1splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    var isUserLoggedIn = MutableLiveData<Boolean>()
    var tokenRefreshed = MutableLiveData<Resource<Boolean>>()
    fun isUserLoggedIn() {
        isUserLoggedIn.value = dataManager.getUserLoggedIn()
    }

    fun refreshToken() {
        viewModelScope.launch {
            tokenRefreshed.value = Resource.Loading

            val response = safeApiCall {
                dataManager.refreshToken(
                    dataManager.getUserId(),
                    dataManager.getAccessToken()
                )
            }

            when (response) {
                is Resource.Success -> {
                    val jsonObject = response.data.asJsonObject

                    when (jsonObject["session"].asString) {
                        SplashActivity.TOKEN_ACTIVE -> {
                            dataManager.setAccessToken(jsonObject["refreshed_token"].asString)
                            tokenRefreshed.value = Resource.Success(true)
                        }
                        SplashActivity.TOKEN_EXPIRED -> {
                            tokenRefreshed.value = Resource.Success(false)
                        }
                    }
                }
                is Resource.Error -> {
                    tokenRefreshed.value = response
                }
            }
        }
    }

}