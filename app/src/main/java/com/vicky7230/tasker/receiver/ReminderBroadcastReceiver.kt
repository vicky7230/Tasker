package com.vicky7230.tasker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val builder = NotificationCompat.Builder(context, "Notify_Task")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Tasker")
            .setContentText(intent.getStringExtra(NewTaskActivity.EXTRAS_TASK))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val nm = NotificationManagerCompat.from(context)
        nm.notify(200, builder.build())
    }

}