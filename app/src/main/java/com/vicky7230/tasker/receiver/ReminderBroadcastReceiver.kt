package com.vicky7230.tasker.receiver

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vicky7230.tasker.R
import com.vicky7230.tasker.receiver.NotificationConfig.NOTIFICATION_ID
import com.vicky7230.tasker.receiver.NotificationConfig.PENDING_INTENT_REQUEST_CODE
import com.vicky7230.tasker.ui._1splash.SplashActivity
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val mIntent = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            PENDING_INTENT_REQUEST_CODE,
            mIntent,
            getPendingIntentGetActivityFlag()
        )
        val notificationBuilder =
            NotificationCompat.Builder(context, context.getString(R.string.notify_task))
                .setSmallIcon(R.drawable.ic_tasker_notification)
                .setContentTitle(context.getString(R.string.pending_task))
                .setContentText(intent.getStringExtra(NewTaskActivity.EXTRAS_TASK))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getPendingIntentGetActivityFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        else
            PendingIntent.FLAG_UPDATE_CURRENT
}