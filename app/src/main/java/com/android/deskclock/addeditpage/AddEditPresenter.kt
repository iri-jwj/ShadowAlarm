package com.android.deskclock.addeditpage

import android.content.Context
import com.android.deskclock.BasePresenter
import com.android.deskclock.model.ShadowAlarm
import java.lang.StringBuilder
import java.util.*

class AddEditPresenter(private val action: String, private val context: Context) : BasePresenter() {

    private lateinit var mAlarm: ShadowAlarm

    override fun start() {
        val calendar = Calendar.getInstance()
        mAlarm = ShadowAlarm(
            UUID.randomUUID(),
            "闹钟",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            0,
            0b11,
            "${context.dataDir}/马林巴琴.mp3",
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
        when (remindDaysInWeek) {
            0b0111110 -> builder.append("每工作日")
            0b1000001 -> builder.append("每周末")
            0b1111111 -> builder.append("每天")
            else -> for (i in 1..7) {
                var temp = 1
                temp = temp.shl(i - 1)
                if (remindDaysInWeek.and(temp) == temp) {
                    builder.append(getTargetWeekDay(i))
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
        if (!mAlarm.isEnabled) {
            mAlarm.isEnabled = true
        }
        return mAlarm
    }

    fun saveNewEditRepeat(repeat: Int) {
        mAlarm.remindDaysInWeek = repeat
    }

    fun saveNewEditedLabel(label: String) {
        mAlarm.label = label
    }

    fun getRemindDaysInWeek(): Int {
        return mAlarm.remindDaysInWeek
    }

    fun getRemindAction(): Int {
        return mAlarm.remindAction
    }

    fun saveNewRemindAction(action: Int) {
        mAlarm.remindAction = action
    }

    fun getRemindActionText(): String {
        val builder = StringBuilder()
        if (mAlarm.remindAction.and(0b01) != 0) {
            builder.append("响铃")
        }

        if (mAlarm.remindAction.and(0b10) != 0) {
            builder.append(" 震动")
        }
        return builder.toString()
    }
}