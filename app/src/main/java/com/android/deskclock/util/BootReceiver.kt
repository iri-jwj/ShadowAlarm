package com.android.deskclock.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.deskclock.homepage.HomePagePresenter
import java.util.*

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("ShadowBoot", "start")
                AlarmManagerUtil.setUpWithContext(context!!)
                handleSystemStart(context)
            }
            Intent.ACTION_SHUTDOWN -> {
                Log.d("ShadowBoot", "shutdown")
                handleSystemShutdown(context)
            }
            Intent.ACTION_REBOOT -> {
                Log.d("ShadowBoot", "reboot")
                handleSystemShutdown(context)
            }
        }
    }

    private fun handleSystemShutdown(context: Context?) {
        SPHelper.saveNewShutDownTime(context!!)
    }

    private fun handleSystemStart(context: Context?) {
        //val shutdownTime = SPHelper.getShutDownTime(context!!)
        //Log.d("ShadowBoot", "time = $shutdownTime")

        /*if (shutdownTime != 0L) {
            val presenter = HomePagePresenter(context)
            presenter.start()
            val list = presenter.getAlarmList().filter {
                it.isEnabled
            }
            if (list.isNotEmpty()) {
                for (alarm in list) {
                    when (alarm.remindAction) {
                        0 -> {
                            // 非重复闹钟
                            val alarmTime =
                                getTargetTimeInMills(alarm.remindHours, alarm.remindMinutes)
                            if (shutdownTime > alarmTime || System.currentTimeMillis() < alarmTime) {
                                //当关机时间已经比闹钟时间晚
                                //或开机时间比闹钟时间早
                                AlarmManagerUtil.cancelAlarm(alarm)
                                AlarmManagerUtil.setAlarm(alarm)
                            } else {
                                AlarmManagerUtil.cancelAlarm(alarm)
                                val newAlarm = alarm.getNewCopy()
                                newAlarm.isEnabled = false
                                presenter.updateAlarm(newAlarm, false)
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }*/

        val presenter = HomePagePresenter(context!!)
        presenter.start()
        val list = presenter.getAlarmList().filter {
            it.isEnabled
        }
        Log.d("ShadowBoot", "listSize = ${list.size}")


        if (list.isNotEmpty()) {
            for (alarm in list) {
                val alarmTime =
                    getTargetTimeInMills(alarm.remindHours, alarm.remindMinutes)
                Log.d("ShadowBoot", "alarmTime = ${System.currentTimeMillis() - alarmTime}")
                if (System.currentTimeMillis() < alarmTime) {
                    //当关机时间已经比闹钟时间晚
                    //或开机时间比闹钟时间早
                    AlarmManagerUtil.cancelAlarm(alarm)
                    AlarmManagerUtil.setAlarm(alarm)
                } else {
                    AlarmManagerUtil.cancelAlarm(alarm)
                    val newAlarm = alarm.getNewCopy()
                    newAlarm.isEnabled = false
                    presenter.updateAlarm(newAlarm, false)
                }
            }
        }
    }

    private fun getTargetTimeInMills(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
            hour,
            minute,
            0
        )
        return calendar.timeInMillis
    }
}