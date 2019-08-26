package com.android.deskclock.homepage

import android.os.Bundle
import com.android.deskclock.BaseView
import com.android.deskclock.R

class MainActivity : BaseView() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
