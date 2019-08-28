package com.android.deskclock.addeditpage

import com.android.deskclock.BasePresenter
import com.android.deskclock.model.ShadowAlarm
import java.lang.StringBuilder
import java.util.*

class AddEditPresenter(private val action: String) : BasePresenter() {

    private lateinit var mAlarm: ShadowAlarm

    override fun start() {
        val calendar = Calendar.getInstance()
        mAlarm = ShadowAlarm(
            UUID.randomUUID(),
            "闹钟",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            0,
            true
        )
    }

    fun setNeedEditAlarm(alarm: ShadowAlarm) {
        if (action == AddEditAct.editAction) {
            mAlarm = alarm
        }
    }

    fun getRepeatDays(): String {
        return if (mAlarm.remindDaysInWeek == 0) {
            ""
        } else {
            getFormattedLabelString(mAlarm.remindDaysInWeek)
        }
    }


    private fun getFormattedLabelString(remindDaysInWeek: Int): String {
        val builder = StringBuilder()
        when {
            remindDaysInWeek.and(0x0111110) == 0x0111110 -> builder.append("每工作日")
            remindDaysInWeek.and(0x1000001) == 0x1000001 -> builder.append("每周末")
            else -> for (i in 1..7) {
                var temp = 1
                temp = temp.shl(i - 1)
                if (remindDaysInWeek.and(temp) == temp) {
                    builder.append(getTargetWeekDay(temp))
                    builder.append(" ")
                }
            }
        }
        return builder.toString()
    }

    private fun getTargetWeekDay(weekday: Int): String {
        return when (weekday) {
            1 -> {
                "周日"
            }
            2 -> {
                "周一"
            }
            3 -> {
                "周二"
            }
            4 -> {
                "周三"
            }
            5 -> {
                "周四"
            }
            6 -> {
                "周五"
            }
            7 -> {
                "周六"
            }
            else -> {
                ""
            }
        }
    }

    fun getNewAlarmHour(): Int {
        return mAlarm.remindHours
    }

    fun getNewAlarmMinute(): Int {
        return mAlarm.remindMinutes
    }

    fun getNewAlarmLabel(): String {
        return mAlarm.label
    }

    fun saveNewEditHour(newVal: Int) {
        mAlarm.remindHours = newVal
    }

    fun saveNewEditMinute(newVal: Int) {
        mAlarm.remindMinutes = newVal
    }

    fun getResultAlarm(): ShadowAlarm {
        return mAlarm
    }

    fun saveNewEditRepeat(repeat: Int) {
        mAlarm.remindDaysInWeek = repeat
    }
}