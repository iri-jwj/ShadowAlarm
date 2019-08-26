package com.android.deskclock

import androidx.appcompat.app.AppCompatActivity

abstract class BaseView<T> : AppCompatActivity(){
    open fun setPresenter(presenter: BasePresenter){

    }
}