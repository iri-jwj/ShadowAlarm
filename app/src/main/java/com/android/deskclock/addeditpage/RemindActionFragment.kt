package com.android.deskclock.addeditpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.deskclock.R
import com.android.deskclock.customview.UselessToolbar

class RemindActionFragment(private val hasSelectedRemindAction: Int) : Fragment() {


    companion object {
        private lateinit var instance: RemindActionFragment
        private lateinit var mManager: FragmentManager
        private lateinit var selectDayCallback: (Int) -> Unit
        private var oldResult: Int = 0

        fun setUpFragment(
            manager: FragmentManager, container: Int, hasSelectedRemindAction: Int,
            callback: (Int) -> Unit
        ) {
            oldResult = hasSelectedRemindAction
            mManager = manager
            selectDayCallback = callback
            instance = RemindActionFragment(hasSelectedRemindAction)
            mManager.beginTransaction()
                .setCustomAnimations(R.anim.fragmen_slide_in, 0)
                .add(container, instance, AddEditAct.selectRepeatTag)
                .commit()
        }

        private fun hideSelf(result: Int) {
            selectDayCallback(result)
            mManager.beginTransaction().setCustomAnimations(0, R.anim.fragment_slide_out)
                .remove(instance).commit()
        }
    }


    private var selectedActionResult = hasSelectedRemindAction

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_selete_remind_action, container, false)
        initViewState(rootView)
        initListener(rootView)
        return rootView
    }

    private fun initViewState(rootView: View) {
        val remindAlarm = rootView.findViewById<ViewGroup>(R.id.select_alarm)
        val remindVibration = rootView.findViewById<ViewGroup>(R.id.select_vibration)

        if (hasSelectedRemindAction.and(0b01) != 0) {
            remindAlarm.findViewById<ImageView>(R.id.is_remind_action_alarm).visibility =
                View.VISIBLE
        } else {
            remindAlarm.findViewById<ImageView>(R.id.is_remind_action_alarm).visibility = View.GONE
        }

        if (hasSelectedRemindAction.and(0b10) != 0) {
            remindVibration.findViewById<ImageView>(R.id.is_remind_action_vibration).visibility =
                View.VISIBLE
        } else {
            remindVibration.findViewById<ImageView>(R.id.is_remind_action_vibration).visibility =
                View.GONE
        }
    }

    private fun initListener(rootView: View) {
        val toolbar = rootView.findViewById<UselessToolbar>(R.id.toolbar)
        val remindAlarm = rootView.findViewById<ViewGroup>(R.id.select_alarm)
        val remindVibration = rootView.findViewById<ViewGroup>(R.id.select_vibration)
        toolbar.setOnLeftItemClickListener {
            hideSelf(selectedActionResult)
        }

        remindAlarm.setOnClickListener {
            if (selectedActionResult.and(0b01) != 0) {
                selectedActionResult = selectedActionResult.and(0b10)
                remindAlarm.findViewById<ImageView>(R.id.is_remind_action_alarm).visibility =
                    View.GONE
            } else {
                selectedActionResult = selectedActionResult.or(0b01)
                remindAlarm.findViewById<ImageView>(R.id.is_remind_action_alarm).visibility =
                    View.VISIBLE
            }
        }

        remindVibration.setOnClickListener {
            if (selectedActionResult.and(0b10) != 0) {
                selectedActionResult = selectedActionResult.and(0b01)
                remindVibration.findViewById<ImageView>(R.id.is_remind_action_vibration)
                    .visibility =
                    View.GONE
            } else {
                selectedActionResult = selectedActionResult.or(0b10)
                remindVibration.findViewById<ImageView>(R.id.is_remind_action_vibration)
                    .visibility =
                    View.VISIBLE
            }
        }
    }
}