package com.android.deskclock.addeditpage

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.android.deskclock.BaseView
import com.android.deskclock.R
import com.android.deskclock.addeditpage.selectaudio.ScanAudioFileService
import com.android.deskclock.addeditpage.selectaudio.SelectAudioFragment
import com.android.deskclock.customview.UselessToolbar
import com.android.deskclock.model.ShadowAlarm
import java.io.File


class AddEditAct : BaseView<AddEditPresenter>() {

    private lateinit var presenter: AddEditPresenter

    companion object {
        const val editAction = "Action_Edit"
        const val addAction = "Action_Add"

        const val selectRepeatTag = "tag_selectRepeat"
        const val editLabelTag = "tag_editLabel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        val tempIntent = intent
        presenter = AddEditPresenter(tempIntent.action!!, this)
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
        initView()
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

            initView()
        } else {
            handleAddAction()
        }
    }

    private fun initView() {
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
        }

        findViewById<TextView>(R.id.add_edit_repeat).text = presenter.getRepeatDays()

        findViewById<TextView>(R.id.add_edit_label).text = presenter.getNewAlarmLabel()

        findViewById<TextView>(R.id.add_edit_remind_action).text = presenter.getRemindActionText()

        findViewById<TextView>(R.id.add_edit_audio).text = presenter.getRemindAudioName()

        findViewById<ViewGroup>(R.id.add_edit_label_layout).setOnClickListener {
            EditLabelFragment.setUpFragment(
                supportFragmentManager,
                R.id.container,
                presenter.getNewAlarmLabel()
            ) {
                handelNewLabel(it)
            }
        }

        findViewById<ViewGroup>(R.id.add_edit_repeat_layout).setOnClickListener {
            SelectRepeatFragment.setUpFragment(
                supportFragmentManager,
                R.id.container,
                presenter.getRemindDaysInWeek()
            ) {
                handelNewRepeat(it)
            }
        }

        findViewById<ViewGroup>(R.id.add_edit_remind_action_layout).setOnClickListener {
            RemindActionFragment.setUpFragment(
                supportFragmentManager,
                R.id.container,
                presenter.getRemindAction()
            ) {
                handelNewRemindAction(it)
            }
        }


        findViewById<ViewGroup>(R.id.add_edit_audio_layout).setOnClickListener {
            SelectAudioFragment.setUpFragment(
                supportFragmentManager,
                R.id.container,
                File(presenter.getRemindAudioPath())
            ) {
                handleNewAudioFile(it)
            }
        }
    }

    private fun handleNewAudioFile(it: File) {
        presenter.saveNewAudioFile(it)
        findViewById<TextView>(R.id.add_edit_audio).text = it.name
    }

    private fun handelNewRemindAction(action: Int) {
        presenter.saveNewRemindAction(action)
        findViewById<TextView>(R.id.add_edit_remind_action).text = presenter.getRemindActionText()
    }

    private fun handelNewLabel(label: String) {
        presenter.saveNewEditedLabel(label)
        findViewById<TextView>(R.id.add_edit_label).text = presenter.getNewAlarmLabel()
    }

    private fun handelNewRepeat(repeat: Int) {
        presenter.saveNewEditRepeat(repeat)
        findViewById<TextView>(R.id.add_edit_repeat).text = presenter.getRepeatDays()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, ScanAudioFileService::class.java)
                intent.putExtra("filePath", Environment.getExternalStorageDirectory().absolutePath)
                startService(intent)
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)

        if (fragment != null && fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(0, R.anim.fragment_slide_out).remove(fragment).commit()
        } else {
            super.onBackPressed()
        }
    }
}
