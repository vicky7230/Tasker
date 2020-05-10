package com.vicky7230.tasker.ui._3verifyOTP

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class VerifyOtpViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel(dataManager) {

    var resource = MutableLiveData<Resource<JsonElement>>()

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            resource.value = Resource.Loading

            val response = safeApiCall { dataManager.verifyOtp(email, otp) }

            when (response) {
                is Resource.Success -> {

                    val jsonObject = response.data.asJsonObject

                    if (jsonObject["success"].asBoolean) {

                        dataManager.setUserLoggedIn()
                        dataManager.setAccessToken(jsonObject["token"].asString)
                        dataManager.setUserId(jsonObject["user_id"].asString)
                        dataManager.setUserEmail(jsonObject["email"].asString)

                        resource.value = response
                    } else {
                        resource.value =  Resource.Error(IOException(jsonObject.get("message").asString))
                    }
                }
                is Resource.Error -> {
                    resource.value = response
                }
            }
        }
    }
}