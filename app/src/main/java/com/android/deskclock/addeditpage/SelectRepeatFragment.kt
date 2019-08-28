package com.android.deskclock.addeditpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.deskclock.R

class SelectRepeatFragment : Fragment() {

    companion object{
        val instance = SelectRepeatFragment()
        fun setUpFragment( manager:FragmentManager,container:Int){
            if (manager.fragments.contains(instance)) {

            }
        }
    }

    private var selectedDay = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_repeat, container, false)
    }
}