package com.android.deskclock.homepage

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.deskclock.R
import com.android.deskclock.addeditpage.AddEditAct
import com.android.deskclock.model.ShadowAlarm
import com.android.deskclock.util.OverlayWindowUtil


class AlarmsAdapter(private val activity: Activity) :
    RecyclerView.Adapter<AlarmsAdapter.AlarmViewHolder>() {

    companion object {
        const val insertNewAlarm = 1
        const val deleteAlarm = 2
        const val updateAlarm = 3
    }

    private var isFiltered = false
    private var listCopyForFilter: List<ShadowAlarm>? = null

    private var alarmList: List<ShadowAlarm>? = null

    private lateinit var onCheckedChangeCallback: (Boolean, ShadowAlarm) -> Unit

    private lateinit var onItemDeleteCallback: (ShadowAlarm) -> Unit

    fun refreshAlarmList(alarms: List<ShadowAlarm>) {
        if (alarmList == null) {
            alarmList = ArrayList()
        }
        (alarmList as ArrayList).clear()
        (alarmList as ArrayList).addAll(alarms)
        notifyDataSetChanged()
    }

    fun updateAlarmList(alarm: ShadowAlarm, index: Int, action: Int) {
        when (action) {
            insertNewAlarm -> {
                if (isFiltered) {
                    (listCopyForFilter as ArrayList).add(index, alarm)
                    showFilterAlarms(false)
                    notifyDataSetChanged()
                } else {
                    (alarmList as ArrayList).add(index, alarm)
                    notifyItemInserted(index)
                }
            }
            deleteAlarm -> {
                if (isFiltered) {
                    (listCopyForFilter as ArrayList).removeAt(index)
                    val target = alarmList?.find {
                        it.id == alarm.id
                    }
                    if (target != null) {
                        val targetIndex = (alarmList as ArrayList).indexOf(target)
                        (alarmList as ArrayList).removeAt(targetIndex)
                        notifyItemRemoved(targetIndex)
                    }
                } else {
                    (alarmList as ArrayList).removeAt(index)
                    notifyItemRemoved(index)
                }
            }
            updateAlarm -> {
                if (isFiltered) {
                    val needUpdate = listCopyForFilter?.find {
                        it.id == alarm.id
                    }
                    (listCopyForFilter as ArrayList).remove(needUpdate)
                    (listCopyForFilter as ArrayList).add(index, alarm)
                    val filteredList = listCopyForFilter?.filter {
                        it.isEnabled
                    }
                    val filteredIndex = filteredList?.indexOf(alarm)
                    val oldIndex = alarmList?.indexOf(needUpdate)
                    if (alarm.isEnabled) {
                        (alarmList as ArrayList).removeAt(oldIndex!!)
                        (alarmList as ArrayList).add(filteredIndex!!, alarm)
                        notifyItemMoved(oldIndex, filteredIndex)
                        notifyItemChanged(filteredIndex)
                    } else {
                        (alarmList as ArrayList).removeAt(oldIndex!!)
                        notifyItemRemoved(oldIndex)
                    }
                } else {
                    val needUpdate = (alarmList as ArrayList).find {
                        it.id == alarm.id
                    }
                    val oldIndex = alarmList?.indexOf(needUpdate)
                    (alarmList as ArrayList).removeAt(oldIndex!!)
                    (alarmList as ArrayList).add(index, alarm)
                    if (oldIndex != index) {
                        notifyItemMoved(oldIndex, index)
                        notifyItemChanged(index)
                    } else {
                        notifyItemChanged(index)
                    }
                }
            }
        }
    }

    fun showIfFilteredAlarms() {
        isFiltered = !isFiltered
        if (isFiltered) {
            showFilterAlarms(true)
        } else {
            (alarmList as ArrayList).clear()
            (alarmList as ArrayList).addAll(listCopyForFilter!!)
        }
        notifyDataSetChanged()
    }

    private fun showFilterAlarms(isFilterButtonClick: Boolean) {
        if (listCopyForFilter == null) {
            listCopyForFilter = ArrayList()
        }
        alarmList = if (isFilterButtonClick) {
            (listCopyForFilter as ArrayList).clear()
            (listCopyForFilter as ArrayList).addAll(alarmList!!)
            alarmList?.filter {
                it.isEnabled
            }
        } else {
            listCopyForFilter?.filter {
                it.isEnabled
            }
        }
    }

    fun setOnItemDeleteCallback(callback: (ShadowAlarm) -> Unit) {
        onItemDeleteCallback = callback
    }

    fun setOnCheckedChangeCallback(callback: (Boolean, ShadowAlarm) -> Unit) {
        onCheckedChangeCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = activity.layoutInflater.inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (alarmList == null) {
            0
        } else {
            alarmList!!.size
        }
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {

        val alarm = alarmList!![position]
        Log.d("AlarmAdapter", "position:$position + id=${alarm.id} + ")

        holder.getClockTimeTextView().text =
            getFormattedTimeString(alarm.remindHours, alarm.remindMinutes)

        holder.getLabelTextView().text =
            getFormattedLabelString(alarm.label, alarm.remindDaysInWeek)

        holder.getSwitchButton().setOnCheckedChangeListener { _, _ ->

        }

        holder.getSwitchButton().isChecked = alarm.isEnabled

        holder.getSwitchButton().setOnCheckedChangeListener { _, isChecked ->
            Log.d("AlarmAdapter", "position:$position + id=${alarm.id} + ")
            onCheckedChangeCallback(isChecked, alarm)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, AddEditAct::class.java)
            intent.action = AddEditAct.editAction
            intent.putExtra("alarm", alarm)
            activity.startActivityForResult(intent, MainActivity.editAlarmCode)
        }

        holder.itemView.setOnLongClickListener {
            OverlayWindowUtil(
                activity,
                "删除警告",
                "确认删除： ${alarm.label}?"
            ).setOnPositiveClicked {
                onItemDeleteCallback(alarm)
            }.showFloatingView()
            true
        }
    }

    private fun getFormattedLabelString(label: String, remindDaysInWeek: Int): String {
        val builder = StringBuilder()
        builder.append(label)
        builder.append(" ")
        when (remindDaysInWeek) {
            0b0111110 -> builder.append(", 每工作日")
            0b1000001 -> builder.append(", 每周末")
            0b1111111 -> builder.append(", 每天")
            else -> for (i in 1..7) {
                var temp = 1
                temp = temp.shl(i - 1)
                if (remindDaysInWeek.and(temp) == temp) {
                    builder.append(getTargetWeekDay(i))

                }
            }
        }
        return builder.toString()
    }

    private fun getTargetWeekDay(weekday: Int): String {
        return when (weekday) {
            1 -> {
                " 周日"
            }
            2 -> {
                " 周一"
            }
            3 -> {
                " 周二"
            }
            4 -> {
                " 周三"
            }
            5 -> {
                " 周四"
            }
            6 -> {
                " 周五"
            }
            7 -> {
                " 周六"
            }
            else -> {
                ""
            }
        }
    }

    private fun getFormattedTimeString(remindHours: Int, remindMinutes: Int): String {
        val builder = StringBuilder()
        if (remindHours < 10) {
            builder.append("0$remindHours")
        } else {
            builder.append(remindHours)
        }

        builder.append(":")

        if (remindMinutes < 10) {
            builder.append("0$remindMinutes")
        } else {
            builder.append(remindMinutes)
        }
        return builder.toString()

    }

    class AlarmViewHolder(alarmView: View) : RecyclerView.ViewHolder(alarmView) {
        fun getClockTimeTextView(): TextView {
            return itemView.findViewById(R.id.item_clock_time)
        }

        fun getLabelTextView(): TextView {
            return itemView.findViewById(R.id.item_label)
        }

        fun getSwitchButton(): Switch {
            return itemView.findViewById(R.id.item_switch)
        }

    }
}