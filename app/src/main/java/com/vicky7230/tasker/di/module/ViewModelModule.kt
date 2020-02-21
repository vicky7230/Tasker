package com.vicky7230.tasker.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.ViewModelFactory
import com.vicky7230.tasker.di.ViewModelKey
import com.vicky7230.tasker.ui._1splash.SplashViewModel
import com.vicky7230.tasker.ui._2login.LoginViewModel
import com.vicky7230.tasker.ui._3verifyOTP.VerifyOtpViewModel
import com.vicky7230.tasker.ui._4home.HomeViewModel
import com.vicky7230.tasker.ui._5newTask.NewTaskViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun postHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewTaskViewModel::class)
    internal abstract fun postNewTaskViewModel(newTaskViewModel: NewTaskViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun postLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VerifyOtpViewModel::class)
    internal abstract fun postVerifyOtpViewModell(verifyOtpViewModel: VerifyOtpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun postSplashViewModel(splashViewModel: SplashViewModel): ViewModel

}