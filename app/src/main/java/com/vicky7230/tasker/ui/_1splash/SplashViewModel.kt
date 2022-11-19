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
) : BaseViewModel(dataManager) {

}