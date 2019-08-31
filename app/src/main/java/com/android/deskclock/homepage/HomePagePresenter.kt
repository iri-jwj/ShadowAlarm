package com.android.deskclock.homepage

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.deskclock.BasePresenter
import com.android.deskclock.model.AlarmProvider
import com.android.deskclock.model.ShadowAlarm
import com.android.deskclock.model.database.AlarmDatabase
import com.android.deskclock.util.AlarmManagerUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class HomePagePresenter(private val context: Context) : BasePresenter() {

    private val tag = "HomePagePresenter"

    private val uri = Uri.parse("content://${AlarmProvider.AUTHORITY}/${AlarmProvider.PATH}")
    private val resolver = context.contentResolver

    private lateinit var alarmList: List<ShadowAlarm>

    private var isFiltered = false

    override fun start() {
        alarmList = queryAlarms()
        sortAlarmList()
    }

    fun deleteAlarm(shadowAlarm: ShadowAlarm): List<ShadowAlarm> {
        val result = resolver.delete(uri, "id = ?", arrayOf(shadowAlarm.id.toString()))
        if (result != 0) {
            Log.d(tag, "delete target target=${shadowAlarm.id}")
            (alarmList as ArrayList).remove(shadowAlarm)
        } else {
            Log.d(tag, "delete failed")
        }
        if (shadowAlarm.isEnabled) {
            AlarmManagerUtil.cancelAlarm(shadowAlarm)
        }

        return alarmList
    }

    fun updateAlarm(shadowAlarm: ShadowAlarm, checkedClick: Boolean): List<ShadowAlarm> {

        val result = resolver.update(
            uri,
            buildContentValues(shadowAlarm),
            "id = ?",
            arrayOf(shadowAlarm.id.toString())
        )
        if (result > 0) {
            val old = alarmList.find {
                shadowAlarm.id == it.id
            }
            val oldCopy = old!!.getNewCopy()
            old.apply {
                remindHours = shadowAlarm.remindHours
                remindMinutes = shadowAlarm.remindMinutes
                remindDaysInWeek = shadowAlarm.remindDaysInWeek
                label = shadowAlarm.label
                isEnabled = shadowAlarm.isEnabled
            }
            if (!checkedClick) {
                sortAlarmList()
            }
            AlarmManagerUtil.updateAlarm(oldCopy, shadowAlarm)
        }
        return alarmList
    }

    fun addAlarm(shadowAlarm: ShadowAlarm): List<ShadowAlarm> {
        resolver.insert(uri, buildContentValues(shadowAlarm))
        (alarmList as ArrayList).add(shadowAlarm)
        sortAlarmList()
        AlarmManagerUtil.setAlarm(shadowAlarm)
        return alarmList
    }

    fun filterEnabledAlarm(): List<ShadowAlarm> {
        isFiltered = !isFiltered
        return if (isFiltered) alarmList.filter {
            it.isEnabled
        } else {
            alarmList
        }
    }

    private fun sortAlarmList() {
        val list = alarmList.sortedWith(compareBy({ it.remindHours }, { it.remindMinutes }))
        (alarmList as ArrayList).clear()
        list.forEach {
            (alarmList as ArrayList).add(it)
        }
    }

    private fun queryAlarms(): List<ShadowAlarm> {
        val tempList = ArrayList<ShadowAlarm>()
        val cursor = resolver.query(uri, null, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val alarm = ShadowAlarm(
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_LABEL)),
                    cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDHOUR)),
                    cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDMINUTE)),
                    cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDDAYSINWEEK)),
                    cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ENABLED)) == 1
                )
                tempList.add(alarm)
            }
            cursor.close()
        }

        return tempList
    }

    private fun buildContentValues(shadowAlarm: ShadowAlarm): ContentValues {
        val values = ContentValues()
        values.apply {
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID, shadowAlarm.id.toString())
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_LABEL, shadowAlarm.label)
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDHOUR, shadowAlarm.remindHours)
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDMINUTE, shadowAlarm.remindMinutes)
            put(
                AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDDAYSINWEEK,
                shadowAlarm.remindDaysInWeek
            )
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ENABLED, shadowAlarm.isEnabled)
        }
        return values
    }

    fun getAlarmList(): List<ShadowAlarm> {
        return alarmList
    }

    /**
     *
     */
    fun setOnceAlarmFinished(id: Int) {
        for (alarm in alarmList) {
            if (abs(alarm.id.hashCode()) == id && alarm.remindDaysInWeek == 0) {
                alarm.isEnabled = false
                resolver.update(
                    uri,
                    buildContentValues(alarm),
                    "id = ?",
                    arrayOf(alarm.id.toString())
                )
                updateAlarm(alarm,true)
            }
        }
    }
}