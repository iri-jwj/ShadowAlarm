package com.android.deskclock.util

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log

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
            //showAlarmByOverlayWindow(context, label, id)
            showAlarmByActivity(context, label, id)
        }
    }

    private fun showAlarmByActivity(context: Context, label: String?, id: Int) {
        val intent = Intent(context, LockedScreenAlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("label",label)
        intent.putExtra("id",id)
        context.startActivity(intent)
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