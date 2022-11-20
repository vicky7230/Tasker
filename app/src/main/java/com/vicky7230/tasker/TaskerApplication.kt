package com.vicky7230.tasker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import com.vicky7230.tasker.di.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject


class TaskerApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notify Task"
            val description = "Channel for task reminder"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("Notify_Task", name, importance)
            channel.description = description
            channel.enableVibration(true)
            val sound: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.pristine)
            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.enableVibration(true)
            channel.setSound(sound, audioAttributes)
            channel.enableLights(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }


}