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
import java.util.*

class MainActivity : BaseView<HomePagePresenter>() {
    companion object {
        private const val addNewAlarmCode = 100
        const val editAlarmCode = 101
    }

    private val mAdapter = AlarmsAdapter(this)
    private lateinit var mPresenter: HomePagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent != null) {
            val id = intent.getIntExtra(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID, 0)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (id != 0) {
                nm.cancel(id)
            }
        }

        AlarmManagerUtil.setUpWithContext(this)
        mPresenter = HomePagePresenter(this)
        mPresenter.start()

        val toolbar = findViewById<UselessToolbar>(R.id.toolbar)
        toolbar.apply {
            setOnLeftItemClickListener {
                mAdapter.refreshAlarmList(mPresenter.filterEnabledAlarm())
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
                    shadowAlarm.isEnabled = b
                    mAdapter.refreshAlarmList(mPresenter.updateAlarm(shadowAlarm))
                }
            }
            setOnItemDeleteCallback {
                mAdapter.refreshAlarmList(mPresenter.deleteAlarm(it))
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

    override fun setPresenter(presenter: HomePagePresenter) {
        super.setPresenter(presenter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                addNewAlarmCode -> {
                    val alarm = data?.getParcelableExtra<ShadowAlarm>("alarm")
                    if (alarm != null) {
                        mAdapter.refreshAlarmList(mPresenter.addAlarm(alarm))
                    }

                }

                editAlarmCode -> {
                    val alarm = data?.getParcelableExtra<ShadowAlarm>("alarm")
                    if (alarm != null)
                        mAdapter.refreshAlarmList(mPresenter.updateAlarm(alarm))
                }
            }
        }
    }
}
