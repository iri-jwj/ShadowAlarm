package com.android.deskclock.util

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.android.deskclock.homepage.HomePagePresenter
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DELAY = "com.android.deskclock.actionDelay"
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent!!.action) {
            ACTION_DELAY -> {
                handleDelayAlarmAction(context, intent)
            }
            AlarmManagerUtil.alarmAction -> {
                handleNormalAlarmAction(context, intent)
            }
        }
    }

    private fun handleDelayAlarmAction(context: Context?, intent: Intent) {
        val newIntent = Intent(context, AlarmReceiver::class.java)
        newIntent.action = AlarmManagerUtil.alarmAction
        newIntent.putExtra("id", intent.getIntExtra("id", 0))
        newIntent.putExtra("label", intent.getStringExtra("label"))
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            intent.getIntExtra("id", 0),
            newIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val calender = Calendar.getInstance()
        calender.set(
            calender[Calendar.YEAR],
            calender[Calendar.MONTH],
            calender[Calendar.DAY_OF_MONTH],
            calender[Calendar.HOUR_OF_DAY],
            calender[Calendar.MINUTE] + 5,
            0
        )
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calender.timeInMillis,
            pendingIntent
        )
    }

    private fun handleNormalAlarmAction(context: Context?, intent: Intent) {
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenLighted = powerManager.isInteractive
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isLocked = keyguardManager.isKeyguardLocked

        val label = intent.getStringExtra("label")
        val id = intent.getIntExtra("id", 0)
        val remindAction = intent.getIntExtra("remindAction", 0)
        if (isScreenLighted and !isLocked) {
            showAlarmByNotification(context, label, id, remindAction)
        } else {
            //showAlarmByOverlayWindow(context, label, id)
            showAlarmByActivity(context, label, id, remindAction)
        }
    }

    private fun showAlarmByActivity(context: Context, label: String?, id: Int, remindAction: Int) {
        val intent = Intent(context, LockedScreenAlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("label", label)
        intent.putExtra("id", id)
        intent.putExtra("remindAction", remindAction)
        context.startActivity(intent)
    }


    private fun showAlarmByOverlayWindow(context: Context, label: String?, id: Int?) {
        val intent = Intent(context, AlarmOverlayService::class.java)
        intent.putExtra("label", label)
        intent.putExtra("id", id)
        context.startService(intent)
    }

    private fun showAlarmByNotification(
        context: Context,
        label: String?,
        id: Int?,
        remindAction: Int
    ) {

        val presenter = HomePagePresenter(context)
        presenter.start()
        presenter.setOnceAlarmFinished(id!!)
        AlarmNotificationUtil(context, label!!, id, remindAction).showNotification()
    }


}