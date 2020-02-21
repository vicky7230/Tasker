package com.vicky7230.tasker.di.module

import com.vicky7230.tasker.ui._1splash.SplashActivity
import com.vicky7230.tasker.ui._1splash.SplashModule
import com.vicky7230.tasker.ui._2login.LoginActivity
import com.vicky7230.tasker.ui._2login.LoginModule
import com.vicky7230.tasker.ui._3verifyOTP.VerifyOtpActivity
import com.vicky7230.tasker.ui._3verifyOTP.VerifyOtpModule
import com.vicky7230.tasker.ui._4home.HomeActivity
import com.vicky7230.tasker.ui._4home.HomeModule
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity
import com.vicky7230.tasker.ui._5newTask.NewTaskModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by vicky on 1/1/18.
 */
@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [(HomeModule::class)])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [(NewTaskModule::class)])
    abstract fun bindNewTaskActivity(): NewTaskActivity

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(VerifyOtpModule::class)])
    abstract fun bindVerifyOtpActivity(): VerifyOtpActivity

    @ContributesAndroidInjector(modules = [(SplashModule::class)])
    abstract fun bindSplashActivity(): SplashActivity

}