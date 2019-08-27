package com.android.deskclock.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.deskclock.AlarmReceiver
import com.android.deskclock.model.ShadowAlarm
import java.util.*

@SuppressLint("StaticFieldLeak")
object AlarmManagerUtil {

    const val alarmAction = "com.android.deskclock.alarmAction"

    private const val TAG = "AlarmManagerUtil"

    private lateinit var mContext: Context
    private lateinit var mAlarmManager: AlarmManager
    private const val EVERYDAY: Int = 0x1111111

    fun setUpWithContext(context: Context):AlarmManagerUtil {
        mContext = context.applicationContext
        initData()
        return this
    }

    private fun initData() {
        mAlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun cancelAlarm(id: Int) {
        cancelAlarm(id, 0)
    }

    fun updateAlarm(old: ShadowAlarm, new: ShadowAlarm) {
        val oldFlags = old.remindDaysInWeek
        val newFlags = new.remindDaysInWeek
        val resultFlags = calUpdatedFlags(oldFlags, newFlags)

        cancelAlarm(old.id.hashCode(), resultFlags[1])
        setUpAlarms(
            new.id.hashCode(),
            new.label,
            new.remindHours,
            new.remindMinutes,
            resultFlags[1]
        )
    }

    private fun calUpdatedFlags(oldFlags: Int, newFlags: Int): IntArray {
        val result = IntArray(2)
        val changedFlags = oldFlags.xor(newFlags)
        var needCancelFlags = 0
        var needAddFlags = 0
        for (i in 0..6) {
            var temp = 1
            temp = temp.shl(i)
            if (changedFlags.and(temp) != 0) {
                if (temp.and(oldFlags) != 0) {
                    needCancelFlags = needCancelFlags.or(temp)
                } else {
                    needAddFlags = needAddFlags.or(temp)
                }
            }
        }
        result[0] = needAddFlags
        result[1] = needCancelFlags
        return result
    }

    private fun cancelAlarm(id: Int, needCancelFlags: Int) {
        if (needCancelFlags == 0) {
            val intent = Intent(alarmAction)
            val pendingIntent =
                PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_NO_CREATE)
            mAlarmManager.cancel(pendingIntent)
        } else {
            for (i in 0..6) {
                var temp = 1
                temp = temp.shl(i)
                if (needCancelFlags.and(temp) != 0) {
                    val intent = Intent(alarmAction)
                    val pendingIntent =
                        PendingIntent.getBroadcast(
                            mContext,
                            id + temp,
                            intent,
                            PendingIntent.FLAG_NO_CREATE
                        )
                    mAlarmManager.cancel(pendingIntent)
                }
            }
        }
    }


    fun setAlarm(model: ShadowAlarm) {
        val id = model.id.hashCode()
        setUpAlarms(id, model.label, model.remindHours, model.remindMinutes, model.remindDaysInWeek)
        Log.d(TAG,"in set Alarm")
    }

    private fun setUpAlarms(id: Int, label: String, hour: Int, minutes: Int, remindFlags: Int) {
        val intent = Intent(mContext,AlarmReceiver::class.java)
        intent.action = alarmAction
        intent.putExtra("id", id)
        intent.putExtra("label", label)

        val calendar = Calendar.getInstance()
        if ((calendar[Calendar.HOUR_OF_DAY] > hour) or
            ((calendar[Calendar.HOUR_OF_DAY] == hour) and (calendar[Calendar.MINUTE] > minutes))
        ) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            Log.d(TAG,"add one day")
        }

        calendar.set(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
            hour,
            minutes,
            0
        )

        Log.d(TAG,"${calendar[Calendar.HOUR_OF_DAY]}  ${calendar[Calendar.MINUTE]}")

        if (remindFlags == 0) {
            val pendingIntent =
                PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_NO_CREATE)
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Log.d(TAG,"in set once alarm time = ${calendar.timeInMillis - System.currentTimeMillis()}")
        } else {
            if (remindFlags == EVERYDAY) {
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        mContext,
                        id,
                        intent,
                        PendingIntent.FLAG_NO_CREATE
                    )
                mAlarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    24 * 60 * 60 * 1000,
                    pendingIntent
                )
                Log.d(TAG,"in set repeat alarm")

            } else {
                for (i in 1..7) {
                    var temp = 1
                    temp = temp.shl(i - 1)
                    if (remindFlags.and(temp) != 0) {
                        val triggerMills = calExactTriggerMills(i, hour, minutes)
                        val pendingIntent =
                            PendingIntent.getBroadcast(
                                mContext,
                                id + temp,
                                intent,
                                PendingIntent.FLAG_NO_CREATE
                            )
                        mAlarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            triggerMills,
                            7 * 24 * 60 * 60 * 1000,
                            pendingIntent
                        )
                    }
                }
                Log.d(TAG,"in set repeat alarm")


            }
        }
    }

    private fun calExactTriggerMills(dayInWeekFromUser: Int, hours: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        val dayInWeek = calendar[Calendar.DAY_OF_WEEK]
        var daysToAdd = 0
        when {
            dayInWeek < dayInWeekFromUser -> daysToAdd = dayInWeekFromUser - dayInWeek
            dayInWeek > dayInWeekFromUser -> daysToAdd = dayInWeekFromUser - dayInWeek + 7
            else -> {
                if ((calendar[Calendar.HOUR_OF_DAY] > hours) or
                    ((calendar[Calendar.HOUR_OF_DAY] == hours) and (calendar[Calendar.MINUTE] > minutes))
                ) {
                    daysToAdd = 1
                }
            }
        }

        calendar.add(Calendar.DATE, daysToAdd)
        calendar.set(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
            hours,
            minutes
        )

        return calendar.timeInMillis
    }


}