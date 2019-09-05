package com.android.deskclock.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.deskclock.model.ShadowAlarm
import java.util.*
import kotlin.math.abs

@SuppressLint("StaticFieldLeak")
object AlarmManagerUtil {

    const val alarmAction = "com.android.deskclock.alarmAction"

    private const val TAG = "AlarmManagerUtil"

    private lateinit var mContext: Context
    private lateinit var mAlarmManager: AlarmManager
    private const val EVERYDAY: Int = 0b1111111

    fun setUpWithContext(context: Context): AlarmManagerUtil {
        mContext = context.applicationContext
        initData()
        return this
    }

    private fun initData() {
        mAlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun cancelAlarm(alarm: ShadowAlarm) {
        cancelAlarm(alarm, alarm.remindDaysInWeek)
    }

    fun updateAlarm(old: ShadowAlarm, new: ShadowAlarm) {
        val oldFlags = old.remindDaysInWeek
        val newFlags = new.remindDaysInWeek
        var needReplace = false
        if (old.label != new.label ||
            old.remindHours != new.remindHours ||
            old.remindMinutes != new.remindMinutes ||
            old.remindAction != new.remindAction ||
            old.remindAudioPath != new.remindAudioPath
        ) {
            needReplace = true
        }

        var resultFlags: IntArray? = null

        if (oldFlags != newFlags) {
            resultFlags = calUpdateFlags(oldFlags, newFlags)
            needReplace = true
        }

        if (needReplace && resultFlags == null) {
            when {
                old.isEnabled == new.isEnabled && old.isEnabled -> {
                    cancelAlarm(old)
                    setAlarm(new)
                }
                old.isEnabled -> {
                    cancelAlarm(old)
                }
                new.isEnabled -> setAlarm(new)
            }
            return
        } else if (needReplace && resultFlags != null) {
            when {
                old.isEnabled == new.isEnabled && old.isEnabled -> {
                    cancelAlarm(old, resultFlags[1])
                    setUpAlarms(
                        getIntent(mContext, new, AlarmReceiver::class.java),
                        new.id.hashCode(),
                        new.remindHours,
                        new.remindMinutes,
                        resultFlags[0]
                    )
                }
                old.isEnabled -> {
                    cancelAlarm(old)
                }
                new.isEnabled -> setAlarm(new)
            }
            return
        }

        if (new.isEnabled != old.isEnabled) {
            if (new.isEnabled) {
                setAlarm(new)
            } else {
                cancelAlarm(old)
            }
        }
    }

    private fun calUpdateFlags(oldFlags: Int, newFlags: Int): IntArray {
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

    private fun cancelAlarm(shadowAlarm: ShadowAlarm, needCancelFlags: Int) {
        val intId = abs(shadowAlarm.id.hashCode())

        if (needCancelFlags == 0) {

            val intent = getIntent(mContext, shadowAlarm, AlarmReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(mContext, intId, intent, PendingIntent.FLAG_ONE_SHOT)
            mAlarmManager.cancel(pendingIntent)
        } else {
            for (i in 0..6) {
                var temp = 1
                temp = temp.shl(i)
                if (needCancelFlags.and(temp) != 0) {
                    val intent = getIntent(mContext, shadowAlarm, AlarmReceiver::class.java)
                    val pendingIntent =
                        PendingIntent.getBroadcast(
                            mContext,
                            intId + temp,
                            intent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    mAlarmManager.cancel(pendingIntent)
                }
            }
        }
    }


    fun setAlarm(model: ShadowAlarm) {
        val id = abs(model.id.hashCode())
        setUpAlarms(
            getIntent(mContext, model, AlarmReceiver::class.java),
            id,
            model.remindHours,
            model.remindMinutes,
            model.remindDaysInWeek
        )
        Log.d(TAG, "in set Alarm")
    }

    private fun setUpAlarms(
        intent: Intent,
        id: Int,
        hour: Int,
        minutes: Int,
        remindFlags: Int
    ) {
        val calendar = Calendar.getInstance()
        if ((calendar[Calendar.HOUR_OF_DAY] > hour) or
            ((calendar[Calendar.HOUR_OF_DAY] == hour) and (calendar[Calendar.MINUTE] > minutes))
        ) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            Log.d(TAG, "add one day")
        }

        calendar.set(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
            hour,
            minutes,
            0
        )

        if (remindFlags == 0) {
            val pendingIntent =
                PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_ONE_SHOT)
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            if (remindFlags == EVERYDAY) {
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        mContext,
                        id,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                    )
                mAlarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    24 * 60 * 60 * 1000,
                    pendingIntent
                )

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
                                PendingIntent.FLAG_ONE_SHOT
                            )
                        mAlarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            triggerMills,
                            7 * 24 * 60 * 60 * 1000,
                            pendingIntent
                        )
                    }
                }
            }
        }
    }

    private fun checkIsNearestAlarm(){
        val current = System.currentTimeMillis()

    }

    private fun <T : Any> getIntent(
        context: Context,
        shadowAlarm: ShadowAlarm,
        targetClass: Class<T>
    ): Intent {
        val intent = Intent(context, targetClass)
        intent.action = alarmAction
        intent.putExtra("id", abs(shadowAlarm.id.hashCode()))
        intent.putExtra("label", shadowAlarm.label)
        intent.putExtra("remindAction", shadowAlarm.remindAction)
        intent.putExtra("audio", shadowAlarm.remindAudioPath)
        return intent
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