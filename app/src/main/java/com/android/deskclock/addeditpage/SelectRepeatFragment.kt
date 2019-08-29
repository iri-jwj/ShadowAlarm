package com.android.deskclock.addeditpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.deskclock.R
import com.android.deskclock.customview.UselessToolbar

class SelectRepeatFragment(private val hasSelectedDays: Int) : Fragment() {

    companion object {
        private lateinit var instance: SelectRepeatFragment
        private lateinit var mManager: FragmentManager
        private lateinit var selectDayCallback: (Int) -> Unit
        private var oldDays: Int = 0

        fun setUpFragment(
            manager: FragmentManager, container: Int, selectedDays: Int,
            callback: (Int) -> Unit
        ) {
            oldDays = selectedDays
            mManager = manager
            selectDayCallback = callback
            instance = SelectRepeatFragment(selectedDays)
            mManager.beginTransaction().add(container, instance, AddEditAct.selectRepeatTag)
                .commit()


        }

        private fun hideSelf(result: Int) {
            selectDayCallback(result)
            mManager.beginTransaction().remove(instance).commit()
        }
    }

    private var selectedDay = hasSelectedDays
    private val checkedList = ArrayList<Boolean>(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (i in 0..6) {
            checkedList.add(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_select_repeat, container, false)
        view.findViewById<UselessToolbar>(R.id.toolbar).setOnLeftItemClickListener {
            hideSelf(selectedDay)
        }

        handelHasSelectedDays(view)

        view.apply {
            findViewById<RelativeLayout>(R.id.select_monday).setOnClickListener {
                val temp = 0b0000010
                if (checkedList[1]) {
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_mon).visibility = View.GONE
                } else {
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_mon).visibility = View.VISIBLE
                }

                checkedList[1] = !checkedList[1]
            }
            findViewById<RelativeLayout>(R.id.select_tuesday).setOnClickListener {
                val temp = 0b0000100
                if (checkedList[2]) {
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_tues).visibility = View.GONE
                } else {
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_tues).visibility = View.VISIBLE
                }
                checkedList[2] = !checkedList[2]

            }
            findViewById<RelativeLayout>(R.id.select_wednesday).setOnClickListener {
                val temp = 0b0001000
                if (checkedList[3]) {
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_wed).visibility = View.GONE
                } else {
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_wed).visibility = View.VISIBLE
                }
                checkedList[3] = !checkedList[3]

            }
            findViewById<RelativeLayout>(R.id.select_thursday).setOnClickListener {
                val temp = 0b0010000
                if (checkedList[4]) {
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_thur).visibility = View.GONE
                } else {
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_thur).visibility = View.VISIBLE
                }
                checkedList[4] = !checkedList[4]

            }
            findViewById<RelativeLayout>(R.id.select_friday).setOnClickListener {
                val temp = 0b0100000
                if (checkedList[5]) {
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_fri).visibility = View.GONE
                } else {
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_fri).visibility = View.VISIBLE
                }
                checkedList[5] = !checkedList[5]

            }
            findViewById<RelativeLayout>(R.id.select_saturday).setOnClickListener {
                val temp = 0b1000000
                if (checkedList[6]) {
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_sat).visibility = View.GONE
                } else {
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_sat).visibility = View.VISIBLE
                }
                checkedList[6] = !checkedList[6]

            }
            findViewById<RelativeLayout>(R.id.select_sunday).setOnClickListener {
                val temp = 0b0000001
                if (checkedList[0]) {
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_sun).visibility = View.GONE
                } else {
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_sun).visibility = View.VISIBLE
                }
                checkedList[0] = !checkedList[0]
            }

        }
        return view
    }

    private fun handelHasSelectedDays(view: View?) {
        for (i in 0..6) {
            var temp = 1
            temp = temp.shl(i)
            if (hasSelectedDays.and(temp) == temp) {
                checkedList[i] = true
                showTargetCheckedImage(view, i)
            }
        }
    }

    private fun showTargetCheckedImage(view: View?, i: Int) {
        when (i + 1) {
            1 -> {
                view?.findViewById<ImageView>(R.id.weekday_selected_sun)?.visibility = View.VISIBLE
            }
            2 -> {
                view?.findViewById<ImageView>(R.id.weekday_selected_mon)?.visibility = View.VISIBLE

            }
            3 -> {
                view?.findViewById<ImageView>(R.id.weekday_selected_tues)?.visibility = View.VISIBLE
            }
            4 -> {
                view?.findViewById<ImageView>(R.id.weekday_selected_wed)?.visibility = View.VISIBLE
            }
            5 -> {
                view?.findViewById<ImageView>(R.id.weekday_selected_thur)?.visibility = View.VISIBLE
            }
            6 -> {
                view?.findViewById<ImageView>(R.id.weekday_selected_fri)?.visibility = View.VISIBLE
            }
            7 -> {
                view?.findViewById<ImageView>(R.id.weekday_selected_sat)?.visibility = View.VISIBLE
            }
            else -> {
                //do nothing
            }
        }
    }


}