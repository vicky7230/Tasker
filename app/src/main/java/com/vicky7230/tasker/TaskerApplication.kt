package com.vicky7230.tasker

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.vicky7230.tasker.di.MyWorkerFactory
import com.vicky7230.tasker.di.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class TaskerApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var myWorkerFactory: MyWorkerFactory

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
            .inject(this)

        //DaggerApplicationComponent.builder().build().factory()

        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(myWorkerFactory).build()
        )
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }


}