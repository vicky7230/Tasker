package com.vicky7230.tasker.ui._1splash

import androidx.lifecycle.MutableLiveData
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.ui._0base.BaseViewModel
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    var isUserLoggedIn = MutableLiveData<Boolean>()

    fun isUserLoggedIn() {
        isUserLoggedIn.value = dataManager.getUserLoggedIn()
    }

}