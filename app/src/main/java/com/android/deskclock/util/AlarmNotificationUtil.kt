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
    private var id: Int,
    private val remindAction: Int,
    private val audio: String
) {
    private val channelId = "com.android.deskclock.notification"
    private val channelName = "ShadowAlarm's normal notification"

    private val noAudioChannelId = "com.android.deskclock.noAudioNotification"
    private val noAudioChannelName = "ShadowAlarm's noAudio notification"

    private val noVibrationChannelId = "com.android.deskclock.noVibrationNotification"
    private val noVibrationChannelName = "ShadowAlarm's noVibration notification"

    private val noTipChannelId = "com.android.deskclock.noTipNotification"
    private val noTipChannelName = "ShadowAlarm's noTip notification"

    private lateinit var channel: NotificationChannel
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var builder: Notification.Builder

    init {
        id = kotlin.math.abs(id)
        if (Build.VERSION.SDK_INT >= 26) {
            val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            when {
                remindAction.and(0b11) == 0b11 -> {
                    channel =
                        NotificationChannel(
                            channelId,
                            channelName,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                    channel.apply {
                        enableVibration(true)
                        setSound(
                            Uri.parse("android.resource://${context.packageName}/${R.raw.mlbq}"),
                            attribute
                        )
                        vibrationPattern =
                            longArrayOf(200, 400, 200, 400, 200, 400, 200, 400, 200, 400, 200, 400)
                    }
                }
                remindAction.and(0b01) != 0 -> {
                    channel =
                        NotificationChannel(
                            noVibrationChannelId,
                            noVibrationChannelName,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                    channel.apply {
                        enableVibration(false)
                        setSound(
                            Uri.parse("android.resource://${context.packageName}/${R.raw.mlbq}"),
                            attribute
                        )
                    }
                }
                remindAction.and(0b10) != 0 -> {
                    channel =
                        NotificationChannel(
                            noAudioChannelId,
                            noAudioChannelName,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                    channel.apply {
                        setSound(
                            null,
                            null
                        )
                        enableVibration(true)
                        vibrationPattern =
                            longArrayOf(200, 400, 200, 400, 200, 400, 200, 400, 200, 400, 200, 400)
                    }
                }
                else -> {
                    channel = NotificationChannel(
                        noTipChannelId,
                        noTipChannelName,
                        NotificationManager.IMPORTANCE_HIGH

                    )
                    channel.apply {
                        setSound(null, null)
                        enableVibration(false)
                    }
                }
            }
            builder = Notification.Builder(context, getTargetChannelId())
            notificationManager.createNotificationChannel(channel)
        } else {
            builder = Notification.Builder(context)
        }
    }

    private fun getTargetChannelId(): String? {
        return when (remindAction) {
            0b11 -> {
                channelId
            }
            0b01 -> {
                noVibrationChannelId
            }
            0b10 -> {
                noAudioChannelId
            }
            else -> {
                noTipChannelId
            }
        }
    }

    fun showNotification() {
        val remoteView = RemoteViews(context.packageName, R.layout.notifcaion_alarm)

        remoteView.setTextViewText(R.id.notification_label, label)

        val intent = generateIntent()
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        remoteView.setOnClickPendingIntent(R.id.notification_alarm_parent, pendingIntent)

        val notificationBuilder = builder.setCustomContentView(remoteView)
            .setSmallIcon(R.drawable.notification_icon_alarm)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
        //.setOngoing(true)


        if (Build.VERSION.SDK_INT < 26) {
            val uri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.mlbq)
            val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED).build()
            if (remindAction.and(0b01) != 0) {
                notificationBuilder.setSound(uri, attribute)
            }
            if (remindAction.and(0b10) != 0) {
                notificationBuilder.setVibrate(
                    longArrayOf(
                        200,
                        400,
                        200,
                        400,
                        200,
                        400,
                        200,
                        400,
                        200,
                        400,
                        200,
                        400
                    )
                )
            }
        }

        val notification = notificationBuilder.build()
        notification.flags.or(Notification.FLAG_INSISTENT).or(Notification.FLAG_NO_CLEAR)
        notificationManager.notify(id, notification)
    }

    private fun generateIntent(): Intent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.apply {
            action = MainActivity.OPEN_OVERLAY_WINDOW
            putExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID, id)
            putExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_AUDIO, audio)
            putExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_LABEL, label)
            putExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_ACTION, remindAction)
        }
        return intent
    }
}