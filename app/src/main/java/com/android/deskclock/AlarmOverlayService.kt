package com.android.deskclock

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView


class AlarmOverlayService : Service() {

    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val label = intent?.getStringExtra("label")
        val id = intent?.getIntExtra("id", 0)
        showFloatingView(label, id)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showFloatingView(label: String?, id: Int?) {

        checkInfoValid(label, id)

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.overlay_window_alarm, null)
        val windowManager =
            (applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)

        view.apply {
            findViewById<Button>(R.id.overlay_delay).setOnClickListener {
                val intent = Intent(
                    this@AlarmOverlayService,
                    AlarmReceiver::class.java
                )
                intent.action = AlarmReceiver.ACTION_DELAY
                sendBroadcast(intent)
                windowManager.removeView(view)
                turnOffScreen()
            }

            findViewById<TextView>(R.id.overlay_label).text = label

            findViewById<Button>(R.id.overlay_cancel).setOnClickListener {
                windowManager.removeView(view)
                turnOffScreen()
            }
        }

        turnOnScreen()

        val layoutParams = buildLayoutParams(WindowManager.LayoutParams())

        windowManager.addView(
            view,
            layoutParams
        )
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

    private fun buildLayoutParams(params: WindowManager.LayoutParams): WindowManager.LayoutParams {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        params.apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        }
        return params
    }

    private fun checkInfoValid(label: String?, id: Int?) {
        require(!(label == null || id == null)) { "label and id cannot be null" }
    }
}