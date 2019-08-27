package com.android.deskclock.homepage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.deskclock.BaseView
import com.android.deskclock.R
import com.android.deskclock.addeditpage.AddEditAct
import com.android.deskclock.model.ShadowAlarm

class MainActivity : BaseView<HomePagePresenter>() {
    companion object{
        private const val addNewAlarmCode = 100
        const val editAlarmCode = 101
    }

    private val mAdapter = AlarmsAdapter(this)
    private lateinit var mPresenter: HomePagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       mPresenter = HomePagePresenter(this)
        mPresenter.start()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.apply {
            findViewById<ImageView>(R.id.filter_enabled).setOnClickListener {
                mAdapter.refreshAlarmList(mPresenter.filterEnabledAlarm())
            }

            findViewById<ImageView>(R.id.add_alarm).setOnClickListener {
                val intent = Intent(this@MainActivity,AddEditAct::class.java)
                startActivityForResult(intent,addNewAlarmCode)
            }
        }

        mAdapter.apply {
            setOnCheckedChangeCallback { b, shadowAlarm ->
                if (b!= shadowAlarm.isEnabled){
                    shadowAlarm.isEnabled = b
                    mAdapter.refreshAlarmList(mPresenter.updateAlarm(shadowAlarm))
                }
            }
            setOnItemDeleteCallback {
                mAdapter.refreshAlarmList(mPresenter.deleteAlarm(it))
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity,DividerItemDecoration.VERTICAL))
        }
    }

    override fun setPresenter(presenter: HomePagePresenter) {
        super.setPresenter(presenter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){
                addNewAlarmCode->{
                   val alarm = data?.getParcelableExtra<ShadowAlarm>("alarm")
                    if (alarm!=null)
                        mAdapter.refreshAlarmList(mPresenter.addAlarm(alarm))
                }

                editAlarmCode->{
                    val alarm = data?.getParcelableExtra<ShadowAlarm>("alarm")
                    if (alarm!=null)
                        mAdapter.refreshAlarmList(mPresenter.updateAlarm(alarm))
                }
            }
        }
    }
}
