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
        //private const val TAG = "AlarmReceiver"
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
        val newIntent = createNewIntent(
            context!!,
            intent,
            AlarmReceiver::class.java,
            AlarmManagerUtil.alarmAction
        )
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
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
        val audioPath = intent.getStringExtra("audio")
        if (isScreenLighted and !isLocked) {
            showAlarmByNotification(context, label, id, remindAction, audioPath)
        } else {
            showAlarmByActivity(context, intent)
        }
    }

    private fun showAlarmByActivity(context: Context, oldIntent: Intent) {
        val intent =
            createNewIntent(context, oldIntent, LockedScreenAlarmActivity::class.java, null)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun showAlarmByNotification(
        context: Context,
        label: String?,
        id: Int?,
        remindAction: Int,
        audioPath: String
    ) {
        val presenter = HomePagePresenter(context)
        presenter.start()
        presenter.setOnceAlarmFinished(id!!)
        AlarmNotificationUtil(context, label!!, id, remindAction, audioPath).showNotification()
    }

    private fun <T : Any> createNewIntent(
        context: Context,
        oldIntent: Intent,
        targetClass: Class<T>,
        action: String?
    ): Intent {
        val intent = Intent(context, targetClass)
        intent.action = action
        intent.putExtra("id", oldIntent.getIntExtra("id", 0))
        intent.putExtra("label", oldIntent.getStringExtra("label"))
        intent.putExtra("remindAction", oldIntent.getIntExtra("remindAction", 0))
        intent.putExtra("audio", oldIntent.getStringExtra("audio"))
        return intent
    }
}