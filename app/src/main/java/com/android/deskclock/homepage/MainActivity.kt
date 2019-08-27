package com.android.deskclock.homepage

import android.os.Bundle
import com.android.deskclock.BaseView
import com.android.deskclock.R
import com.android.deskclock.model.ShadowAlarm
import com.android.deskclock.util.AlarmManagerUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseView<HomePagePresenter>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test.setOnClickListener {
            AlarmManagerUtil.setUpWithContext(this)
                .setAlarm(ShadowAlarm(UUID.randomUUID(), "testAlarm", 15, 13, 0))
        }
    }




    override fun setPresenter(presenter: HomePagePresenter) {
        super.setPresenter(presenter)
    }
}
