package com.android.deskclock.addeditpage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.TextView
import com.android.deskclock.BaseView
import com.android.deskclock.R
import com.android.deskclock.customview.UselessToolbar
import com.android.deskclock.model.ShadowAlarm


class AddEditAct : BaseView<AddEditPresenter>() {

    private lateinit var presenter: AddEditPresenter

    companion object {
        const val editAction = "Action_Edit"
        const val addAction = "Action_Add"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        val tempIntent = intent
        presenter = AddEditPresenter(tempIntent.action!!)
        presenter.start()
        when (tempIntent.action) {
            editAction -> handleEditAction(tempIntent)
            addAction -> handleAddAction()
        }
    }

    private fun handleAddAction() {

        findViewById<UselessToolbar>(R.id.toolbar).apply {
            setTitle("新建")
            setOnLeftItemClickListener {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            setOnRightItemClickListener {
                val resultAlarm = presenter.getResultAlarm()
                val intent = Intent()
                intent.putExtra("alarm", resultAlarm)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        findViewById<NumberPicker>(R.id.number_picker_hour).apply {
            setFormatter {
                var tmpStr = it.toString()
                if (it < 10) {
                    tmpStr = "0$tmpStr"
                }
                tmpStr
            }
            setOnValueChangedListener { _, oldVal, newVal ->
                if (oldVal != newVal) {
                    presenter.saveNewEditHour(newVal)
                }
            }
            maxValue = 23
            minValue = 0
            value = presenter.getNewAlarmHour()
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
        }
        findViewById<NumberPicker>(R.id.number_picker_minute).apply {
            setFormatter {
                var tmpStr = it.toString()
                if (it < 10) {
                    tmpStr = "0$tmpStr"
                }
                tmpStr
            }
            setOnValueChangedListener { _, oldVal, newVal ->
                if (oldVal != newVal) {
                    presenter.saveNewEditMinute(newVal)
                }
            }
            maxValue = 59
            minValue = 0
            value = presenter.getNewAlarmMinute()
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
        }

        findViewById<TextView>(R.id.add_edit_repeat).text = presenter.getRepeatDays()

        findViewById<TextView>(R.id.add_edit_label).text = presenter.getNewAlarmLabel()
    }

    private fun handleEditAction(tempIntent: Intent?) {
        val alarm = tempIntent?.getParcelableExtra<ShadowAlarm>("alarm")
        if (alarm != null) {

            findViewById<UselessToolbar>(R.id.toolbar).apply {
                setTitle("编辑")
                setOnLeftItemClickListener {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                setOnRightItemClickListener {
                    val resultAlarm = presenter.getResultAlarm()
                    val intent = Intent()
                    intent.putExtra("alarm", resultAlarm)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            presenter.setNeedEditAlarm(alarm)

            findViewById<NumberPicker>(R.id.number_picker_hour).apply {
                setFormatter {
                    var tmpStr = it.toString()
                    if (it < 10) {
                        tmpStr = "0$tmpStr"
                    }
                    tmpStr
                }
                setOnValueChangedListener { _, oldVal, newVal ->
                    if (oldVal != newVal) {
                        presenter.saveNewEditHour(newVal)
                    }
                }
                maxValue = 23
                minValue = 0
                value = alarm.remindHours
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = false
            }
            findViewById<NumberPicker>(R.id.number_picker_minute).apply {
                setFormatter {
                    var tmpStr = it.toString()
                    if (it < 10) {
                        tmpStr = "0$tmpStr"
                    }
                    tmpStr
                }
                setOnValueChangedListener { _, oldVal, newVal ->
                    if (oldVal != newVal) {
                        presenter.saveNewEditMinute(newVal)
                    }
                }
                maxValue = 59
                minValue = 0
                value = alarm.remindMinutes
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = false
            }

            findViewById<TextView>(R.id.add_edit_repeat).text = presenter.getRepeatDays()

            findViewById<TextView>(R.id.add_edit_label).text = alarm.label
        } else {
            handleAddAction()
        }
    }
}
