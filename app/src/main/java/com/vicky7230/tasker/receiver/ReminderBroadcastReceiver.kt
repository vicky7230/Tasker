package com.vicky7230.tasker.receiver

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._1splash.SplashActivity
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val i = Intent(context, SplashActivity::class.java)

        val pi = PendingIntent.getActivity(
            context,
            101,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "Notify_Task")
            .setSmallIcon(R.drawable.ic_tasker_notification)
            .setContentTitle("Pending task")
            .setContentText(intent.getStringExtra(NewTaskActivity.EXTRAS_TASK))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setContentIntent(pi)
            .setAutoCancel(true)

        val nm = NotificationManagerCompat.from(context)
        nm.notify(200, builder.build())
    }

}