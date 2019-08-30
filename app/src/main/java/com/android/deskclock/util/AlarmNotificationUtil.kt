package com.android.deskclock.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.android.deskclock.R
import com.android.deskclock.homepage.MainActivity
import com.android.deskclock.model.database.AlarmDatabase

class AlarmNotificationUtil(
    private val context: Context,
    private val label: String,
    private var id: Int
) {
    private val channelId = "com.android.deskclock.notification"
    private val channelName = "ShadowAlarm's normal notification"
    private lateinit var channel: NotificationChannel
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var builder: Notification.Builder

    init {
        id = kotlin.math.abs(id)
        if (Build.VERSION.SDK_INT >= 26) {
            val uri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.mlbq)
            val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED).build()
            channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.apply {
                enableVibration(true)
                setSound(uri, attribute)
                vibrationPattern = longArrayOf(10, 100, 10, 100, 10, 100)
            }
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
        intent.putExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID, id)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        remoteView.setOnClickPendingIntent(R.id.notification_alarm_parent, pendingIntent)

        val notificationBuilder = builder.setCustomContentView(remoteView)
            .setSmallIcon(R.drawable.notification_icon_alarm)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)


        if (Build.VERSION.SDK_INT < 26) {
            val uri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.mlbq)
            val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED).build()
            notificationBuilder.setSound(uri, attribute)
            notificationBuilder.setVibrate(longArrayOf(10, 100, 10, 100))
        }

        notificationManager.notify(id, notificationBuilder.build())
    }
}