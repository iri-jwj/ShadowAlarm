package com.android.deskclock.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.android.deskclock.R
import com.android.deskclock.homepage.MainActivity

class AlarmNotificationUtil(
    private val context: Context,
    private val label: String,
    private val id: Int
) {
    private val channelId = "com.android.deskclock.notification"
    private val channelName = "ShadowAlarm's normal notification"
    private lateinit var channel: NotificationChannel
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var builder: Notification.Builder

    init {

        if (Build.VERSION.SDK_INT >= 26) {
            channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            builder = Notification.Builder(context, channelId)
            notificationManager.createNotificationChannel(channel)
        } else {
            builder = Notification.Builder(context)
        }


    }

    fun showNotification() {
        val remoteView = RemoteViews(context.packageName, R.layout.notifcaion_alarm)

        remoteView.setTextViewText(R.id.notification_label, label)

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        remoteView.setOnClickPendingIntent(R.id.notification_alarm_parent, pendingIntent)

        val notification = builder.setCustomContentView(remoteView)
            .setSmallIcon(R.drawable.notification_icon_alarm)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true).build()

        notificationManager.notify(id, notification)
    }


}