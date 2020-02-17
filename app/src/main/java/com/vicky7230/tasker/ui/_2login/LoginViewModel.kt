package com.vicky7230.tasker.ui._2login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.network.RetrofitResult
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    var loading = MutableLiveData<Boolean>()
    var error = MutableLiveData<String>()
    var otpGeerated = MutableLiveData<Boolean>()

    fun generateOTP(email: String) {

        otpGeerated.value = true

        /*viewModelScope.launch {
            loading.value = true

            val response = safeApiCall { dataManager.generateOtp(email) }

            when (response) {
                is RetrofitResult.Success -> {

                    val jsonObject = response.data.asJsonObject

                    if (jsonObject.get("success").asBoolean) {
                        otpGeerated.value = true
                    } else {
                        error.value = jsonObject.get("message").asString
                    }
                }
                is RetrofitResult.Error -> {
                    error.value = response.exception.message
                }
            }

            loading.value = false
        }*/
    }
}
