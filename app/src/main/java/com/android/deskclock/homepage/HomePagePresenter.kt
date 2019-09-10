package com.android.deskclock.homepage

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.android.deskclock.BasePresenter
import com.android.deskclock.R
import com.android.deskclock.model.AlarmProvider
import com.android.deskclock.model.ShadowAlarm
import com.android.deskclock.model.database.AlarmDatabase
import com.android.deskclock.util.AlarmManagerUtil
import com.android.deskclock.util.CopyRawToData
import com.android.deskclock.util.OverlayWindowUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class HomePagePresenter(private val context: Context) : BasePresenter,
    ContentObserver(Handler(context.mainLooper)) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onOnceAlarmFinishedCallback()
    }

    override fun deliverSelfNotifications(): Boolean {
        return true
    }

    private val tag = "HomePagePresenter"

    private val uri = Uri.parse("content://${AlarmProvider.AUTHORITY}/${AlarmProvider.PATH}")
    private val resolver = context.contentResolver

    private lateinit var alarmList: List<ShadowAlarm>

    private var isFiltered = false

    private var onOnceAlarmFinishedCallback: () -> Unit = {

    }

    override fun start() {
        alarmList = queryAlarms()
        CopyRawToData.copyRawFile2Data(R.raw.mlbq, context.dataDir, "马林巴琴.mp3", context.resources)
        sortAlarmList()
        resolver.registerContentObserver(uri, true, this)
    }

    fun setCallback(callback: () -> Unit) {
        onOnceAlarmFinishedCallback = callback
    }

    fun deleteAlarm(shadowAlarm: ShadowAlarm): Array<Any> {
        val result = resolver.delete(uri, "id = ?", arrayOf(shadowAlarm.id.toString()))
        var index = 0
        if (result != 0) {
            index = alarmList.indexOf(shadowAlarm)
            (alarmList as ArrayList).remove(shadowAlarm)
        } else {
            Log.d(tag, "delete failed")
        }
        if (shadowAlarm.isEnabled) {
            AlarmManagerUtil.cancelAlarm(shadowAlarm)
        }
        isFiltered = false
        return arrayOf(shadowAlarm, index)
    }

    fun updateAlarm(shadowAlarm: ShadowAlarm, checkedClick: Boolean): Array<Any> {

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
                remindAction = shadowAlarm.remindAction
            }
            if (!checkedClick) {
                sortAlarmList()
            }
            AlarmManagerUtil.updateAlarm(oldCopy, shadowAlarm)
        }
        val index = alarmList.indexOf(shadowAlarm)
        isFiltered = false
        return arrayOf(shadowAlarm, index)
    }

    fun addAlarm(shadowAlarm: ShadowAlarm): Array<Any> {
        resolver.insert(uri, buildContentValues(shadowAlarm))
        (alarmList as ArrayList).add(shadowAlarm)
        sortAlarmList()
        AlarmManagerUtil.setAlarm(shadowAlarm)
        isFiltered = false
        val index = alarmList.indexOf(shadowAlarm)
        return arrayOf(shadowAlarm, index)
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
                    cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_ACTION)),
                    cursor.getString(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_AUDIO)),
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
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_ACTION, shadowAlarm.remindAction)
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_AUDIO, shadowAlarm.remindAudioPath)
            put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ENABLED, shadowAlarm.isEnabled)
        }
        return values
    }

    fun getAlarmList(): List<ShadowAlarm> {
        return alarmList
    }

    fun refreshAlarms(): List<ShadowAlarm> {
        (alarmList as ArrayList).clear()
        alarmList = queryAlarms()
        sortAlarmList()
        return alarmList
    }

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
                updateAlarm(alarm, true)
            }
        }
    }

    fun showIfNeedOverlayWindow(intent: Intent?) {
        if (intent?.action == MainActivity.OPEN_OVERLAY_WINDOW) {
            val id = intent.getIntExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID, 0)
            val label = intent.getStringExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_LABEL)
            val remindAction =
                intent.getIntExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_ACTION, 0)
            val remindAudio =
                intent.getStringExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMIND_AUDIO)

            val util = OverlayWindowUtil(context, label, null)
            util.setAlarmInfo(id, remindAction, remindAudio)
            util.showFloatingView()
        }
    }
}