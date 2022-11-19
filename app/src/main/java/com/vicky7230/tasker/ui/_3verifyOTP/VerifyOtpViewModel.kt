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
}