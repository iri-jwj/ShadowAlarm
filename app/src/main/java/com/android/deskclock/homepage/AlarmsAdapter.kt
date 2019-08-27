package com.android.deskclock.homepage

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.deskclock.R
import com.android.deskclock.addeditpage.AddEditAct
import com.android.deskclock.model.ShadowAlarm
import java.lang.StringBuilder

class AlarmsAdapter(private val activity: Activity) :
    RecyclerView.Adapter<AlarmsAdapter.AlarmViewHolder>() {

    private var alarmList: List<ShadowAlarm>? = null

    private lateinit var onCheckedChangeCallback:(Boolean,ShadowAlarm)->Unit

    private lateinit var onItemDeleteCallback:(ShadowAlarm)->Unit

    fun refreshAlarmList(alarms: List<ShadowAlarm>) {
        alarmList = alarms
        notifyDataSetChanged()
    }

    fun setOnItemDeleteCallback(callback: (ShadowAlarm) -> Unit){
        onItemDeleteCallback = callback
    }

    fun setOnCheckedChangeCallback(callback:(Boolean,ShadowAlarm)->Unit){
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

        holder.getClockTimeTextView().text =
            getFormattedTimeString(alarm.remindHours, alarm.remindMinutes)

        holder.getLabelTextView().text =
            getFormattedLabelString(alarm.label, alarm.remindDaysInWeek)

        holder.getSwitchButton().setOnCheckedChangeListener { _, isChecked ->
            onCheckedChangeCallback(isChecked,alarm)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(activity,AddEditAct::class.java)
            intent.putExtra("alarm",alarm)
            activity.startActivityForResult(intent,MainActivity.editAlarmCode)
        }

        holder.itemView.setOnLongClickListener {
            //todo 长按弹出确认是否删除
            true
        }
    }

    private fun getFormattedLabelString(label: String, remindDaysInWeek: Int): String {
        val builder = StringBuilder()
        builder.append(label)
        when {
            remindDaysInWeek.and(0x0111110) == 0x0111110 -> builder.append(", 每工作日")
            remindDaysInWeek.and(0x1000001) == 0x1000001 -> builder.append(", 每周末")
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

    class AlarmViewHolder(private val alarmView: View) : RecyclerView.ViewHolder(alarmView) {
        fun getClockTimeTextView(): TextView {
            return itemView.findViewById<TextView>(R.id.item_clock_time)
        }

        fun getLabelTextView(): TextView {
            return itemView.findViewById(R.id.item_label)
        }

        fun getSwitchButton(): Switch {
            return itemView.findViewById(R.id.item_switch)
        }

    }
}