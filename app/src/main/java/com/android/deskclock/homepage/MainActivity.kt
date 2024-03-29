package com.android.deskclock.homepage

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.deskclock.BaseView
import com.android.deskclock.R
import com.android.deskclock.addeditpage.AddEditAct
import com.android.deskclock.customview.UselessToolbar
import com.android.deskclock.model.ShadowAlarm
import com.android.deskclock.model.database.AlarmDatabase
import com.android.deskclock.util.AlarmManagerUtil

class MainActivity : BaseView<HomePagePresenter>() {
    companion object {
        private const val addNewAlarmCode = 100
        const val editAlarmCode = 101
        const val OPEN_OVERLAY_WINDOW = "openOverlayWindow"
    }

    private val mAdapter = AlarmsAdapter(this)
    private lateinit var mPresenter: HomePagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPresenter = HomePagePresenter(this)
        mPresenter.start()
        mPresenter.setCallback {
            mAdapter.refreshAlarmList(mPresenter.refreshAlarms())
        }

        if (intent != null) {
            val id = intent.getIntExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID, 0)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (id != 0) {
                nm.cancel(id)
            }
            mPresenter.showIfNeedOverlayWindow(intent)
        }

        AlarmManagerUtil.setUpWithContext(this)

        val toolbar = findViewById<UselessToolbar>(R.id.toolbar)
        toolbar.apply {
            setOnLeftItemClickListener {
                mAdapter.showIfFilteredAlarms()
            }

            setOnRightItemClickListener {
                val intent = Intent(this@MainActivity, AddEditAct::class.java)
                intent.action = AddEditAct.addAction
                startActivityForResult(intent, addNewAlarmCode)
            }
        }

        mAdapter.apply {
            setOnCheckedChangeCallback { b, shadowAlarm ->
                if (b != shadowAlarm.isEnabled) {
                    val alarmCopy = shadowAlarm.getNewCopy()
                    alarmCopy.isEnabled = b
                    val list = mPresenter.updateAlarm(alarmCopy, true)
                    mAdapter.updateAlarmList(
                        list[0] as ShadowAlarm,
                        list[1] as Int,
                        AlarmsAdapter.updateAlarm
                    )
                }
            }
            setOnItemDeleteCallback {
                val list = mPresenter.deleteAlarm(it)
                mAdapter.updateAlarmList(list[0] as ShadowAlarm,list[1] as Int,AlarmsAdapter.deleteAlarm)
            }
            refreshAlarmList(mPresenter.getAlarmList())
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                addNewAlarmCode -> {
                    val alarm = data?.getParcelableExtra<ShadowAlarm>("alarm")
                    if (alarm != null) {
                        val list = mPresenter.addAlarm(alarm)
                        mAdapter.updateAlarmList(
                            list[0] as ShadowAlarm,
                            list[1] as Int,
                            AlarmsAdapter.insertNewAlarm
                        )
                    }
                }
                editAlarmCode -> {
                    val alarm = data?.getParcelableExtra<ShadowAlarm>("alarm")
                    if (alarm != null) {
                        val list = mPresenter.updateAlarm(alarm,false)
                        mAdapter.updateAlarmList(
                            list[0] as ShadowAlarm,
                            list[1] as Int,
                            AlarmsAdapter.updateAlarm
                        )
                    }
                }
            }
        }
    }
}
