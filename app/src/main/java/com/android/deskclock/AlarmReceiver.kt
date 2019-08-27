package com.android.deskclock

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.android.deskclock.util.AlarmManagerUtil
import com.android.deskclock.util.AlarmNotificationUtil

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DELAY = "com.android.deskclock.actionDelay"
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "receive Action : ${intent?.action}")

        when (intent!!.action) {
            ACTION_DELAY -> {

            }
            AlarmManagerUtil.alarmAction -> {
                handleNormalAlarmAction(context, intent)
            }
        }
    }

    private fun handleNormalAlarmAction(context: Context?, intent: Intent) {
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenLighted = powerManager.isInteractive
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isLocked = keyguardManager.isKeyguardLocked

        val label = intent.getStringExtra("label")
        val id = intent.getIntExtra("id", 0)
        if (isScreenLighted and !isLocked) {
            showAlarmByNotification(context, label, id)
        } else {
            showAlarmByOverlayWindow(context, label, id)
        }
    }


    private fun showAlarmByOverlayWindow(context: Context, label: String?, id: Int?) {
        val intent = Intent(context, AlarmOverlayService::class.java)
        intent.putExtra("label", label)
        intent.putExtra("id", id)
        context.startService(intent)
    }

    private fun showAlarmByNotification(context: Context, label: String?, id: Int?) {
        AlarmNotificationUtil(context, label!!, id!!).showNotification()
    }


}