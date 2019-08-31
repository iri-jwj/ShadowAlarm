package com.android.deskclock.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.deskclock.R
import com.android.deskclock.homepage.HomePagePresenter

class LockedScreenAlarmActivity : AppCompatActivity() {

    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locked_screen_alarm)
        configShowWhenLocked()

        val intent = intent
        val label = intent.getStringExtra("label")
        val id = intent.getIntExtra("id", 0)

        checkInfoValid(label, id)


        val util = AlarmNotifyUtil(this)

        util.notifyAudioAndVibrate()

        findViewById<TextView>(R.id.lock_screen_label).text = label

        findViewById<Button>(R.id.lock_screen_cancel).setOnClickListener {
            turnOffScreen()
            val presenter = HomePagePresenter(this)
            presenter.start()
            presenter.setOnceAlarmFinished(id)
            util.stopNotify()
            finish()
        }

        findViewById<Button>(R.id.lock_screen_delay).setOnClickListener {
            turnOffScreen()
            val i = Intent(
                this,
                AlarmReceiver::class.java
            )
            i.putExtra("label", label)
            i.putExtra("id", id)
            i.action = AlarmReceiver.ACTION_DELAY
            sendBroadcast(i)
            util.stopNotify()
            finish()
        }
        turnOnScreen()
    }

    private fun checkInfoValid(label: String?, id: Int) {
        require(!(label == null || id == 0))
    }

    private fun configShowWhenLocked() {
        if (Build.VERSION.SDK_INT < 27)
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        else {
            setShowWhenLocked(true)
        }
    }

    private fun turnOnScreen() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
            "com.android.deskclock:wakelock"
        )
        wakeLock.acquire()//亮屏
    }

    private fun turnOffScreen() {
        wakeLock.release()
    }


}
