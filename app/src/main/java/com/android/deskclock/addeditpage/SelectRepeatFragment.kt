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

class SelectRepeatFragment : Fragment() {

    companion object{
        private val instance = SelectRepeatFragment()
        private lateinit var mManager: FragmentManager
        private lateinit var selectDayCallback:(Int)->Unit

        fun setUpFragment(manager:FragmentManager,container:Int,
                          callback:(Int)->Unit){
            mManager = manager
            selectDayCallback = callback
            val transaction = mManager.beginTransaction()
            if (!mManager.fragments.contains(instance)){
                transaction.add(container,instance,AddEditAct.selectRepeatTag).commit()
            }else{
                transaction.show(instance).commit()
            }

        }

        private fun hideSelf(result : Int){
            selectDayCallback(result)
            mManager.beginTransaction().hide(instance).commit()
        }
    }

    private var selectedDay = 0b0000000
    private val checkedList = ArrayList<Boolean>(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (i in 0..6){
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
        view.apply {
            findViewById<RelativeLayout>(R.id.select_monday).setOnClickListener {
                val temp = 0b0000010
                if (checkedList[1]){
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_mon).visibility = View.GONE
                }else{
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_mon).visibility = View.VISIBLE
                }

                checkedList[1] = !checkedList[1]
            }
            findViewById<RelativeLayout>(R.id.select_tuesday).setOnClickListener {
                val temp = 0b0000100
                if (checkedList[2]){
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_tues).visibility = View.GONE
                }else{
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_tues).visibility = View.VISIBLE
                }
                checkedList[2] = !checkedList[2]

            }
            findViewById<RelativeLayout>(R.id.select_wednesday).setOnClickListener {
                val temp = 0b0001000
                if (checkedList[3]){
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_wed).visibility = View.GONE
                }else{
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_wed).visibility = View.VISIBLE
                }
                checkedList[3] = !checkedList[3]

            }
            findViewById<RelativeLayout>(R.id.select_thursday).setOnClickListener {
                val temp = 0b0010000
                if (checkedList[4]){
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_thur).visibility = View.GONE
                }else{
                    selectedDay =  selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_thur).visibility = View.VISIBLE
                }
                checkedList[4] = !checkedList[4]

            }
            findViewById<RelativeLayout>(R.id.select_friday).setOnClickListener {
                val temp = 0b0100000
                if (checkedList[5]){
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_fri).visibility = View.GONE
                }else{
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_fri).visibility = View.VISIBLE
                }
                checkedList[5] = !checkedList[5]

            }
            findViewById<RelativeLayout>(R.id.select_saturday).setOnClickListener {
                val temp = 0b1000000
                if (checkedList[6]){
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_sat).visibility = View.GONE
                }else{
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_sat).visibility = View.VISIBLE
                }
                checkedList[6] = !checkedList[6]

            }
            findViewById<RelativeLayout>(R.id.select_sunday).setOnClickListener {
                val temp = 0b0000001
                if (checkedList[0]){
                    selectedDay = selectedDay.and(temp.inv())
                    findViewById<ImageView>(R.id.weekday_selected_sun).visibility = View.GONE
                }else{
                    selectedDay = selectedDay.or(temp)
                    findViewById<ImageView>(R.id.weekday_selected_sun).visibility = View.VISIBLE
                }
                checkedList[0] = !checkedList[0]
            }

        }
        return view
    }
}