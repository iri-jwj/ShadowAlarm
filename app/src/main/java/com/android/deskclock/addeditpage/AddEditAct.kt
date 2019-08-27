package com.android.deskclock.addeditpage

import android.os.Bundle
import com.android.deskclock.BaseView
import com.android.deskclock.R

class AddEditAct : BaseView<AddEditPresenter>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
    }
}
