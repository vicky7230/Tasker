package com.vicky7230.tasker.ui._3verifyOTP

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.network.RetrofitResult
import com.vicky7230.tasker.ui._0base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class VerifyOtpViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    var loading = MutableLiveData<Boolean>()
    var error = MutableLiveData<String>()
    var otpVerified = MutableLiveData<Boolean>()

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            loading.value = true

            val response = safeApiCall { dataManager.verifyOtp(email, otp) }

            when (response) {
                is RetrofitResult.Success -> {

                    val jsonObject = response.data.asJsonObject

                    if (jsonObject.get("success").asBoolean) {
                        otpVerified.value = true
                    } else {
                        error.value = jsonObject.get("message").asString
                    }
                }
                is RetrofitResult.Error -> {
                    error.value = response.exception.message
                }
            }

            loading.value = false
        }
    }
}