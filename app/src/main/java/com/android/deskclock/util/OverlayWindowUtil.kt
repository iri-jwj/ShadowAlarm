package com.android.deskclock.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.deskclock.R
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation


class OverlayWindowUtil(
    private val context: Context, private val title: String,
    private val message: String?
) {

    private var onNegativeClicked: () -> Unit = {}
    private var onPositiveClicked: () -> Unit = {}

    fun setOnNegativeClickedListener(onNegativeClicked: () -> Unit): OverlayWindowUtil {
        this.onNegativeClicked = onNegativeClicked
        return this
    }

    fun setOnPositiveClicked(onPositiveClicked: () -> Unit): OverlayWindowUtil {
        this.onPositiveClicked = onPositiveClicked
        return this
    }

    private var alarmId = 0
    private var remindAction = 0
    private var remindAudio = ""
    private lateinit var notifyUtil: AlarmNotifyUtil
    fun setAlarmInfo(id: Int, remindAction: Int, remindAudio: String) {
        this.alarmId = id
        this.remindAction = remindAction
        this.remindAudio = remindAudio
    }

    fun showFloatingView() {
        if (Settings.canDrawOverlays(context)) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.overlay_window_alarm, null)
            val windowManager =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)

            settingUpViews(view, windowManager)

            val xyList = calculateWithAndHeight(windowManager)
            val width = xyList[0] * 0.68
            val height = xyList[1] * 0.32
            val layoutParams =
                buildLayoutParams(WindowManager.LayoutParams(), width.toInt(), height.toInt())

            windowManager.addView(
                view,
                layoutParams
            )

            notifyUtil = AlarmNotifyUtil(context, remindAudio)
            notifyUtil.notifyAudioAndVibrate(remindAction)
        } else {
            tipRequestPermission()
        }
    }

    private fun settingUpViews(view: View, windowManager: WindowManager) {
        view.apply {
            if (message != null && message != "") {
                findViewById<TextView>(R.id.overlay_label).text = message
            } else {
                findViewById<ImageView>(R.id.overlay_image).visibility = View.VISIBLE
                applyAnimation(findViewById(R.id.overlay_image))
            }
            findViewById<TextView>(R.id.overlay_title).text = title

            findViewById<Button>(R.id.overlay_positive).apply {
                setOnClickListener {
                    onPositiveClicked.invoke()
                    if (alarmId != 0) {
                        notifyUtil.stopNotify()
                    }
                    windowManager.removeView(view)
                }
                if (alarmId != 0) {
                    text = context.resources.getText(R.string.remind_later)
                }
            }

            findViewById<Button>(R.id.overlay_negative).apply {
                setOnClickListener {
                    onNegativeClicked.invoke()
                    windowManager.removeView(view)
                    if (alarmId != 0) {
                        notifyUtil.stopNotify()
                    }
                }
                if (alarmId != 0) {
                    text = context.resources.getText(R.string.confirm)
                }
            }
        }
    }

    private fun applyAnimation(alarmImage: ImageView) {
        val alphaAnimation2 = RotateAnimation(
            -10f,
            10f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        alphaAnimation2.duration = 60
        alphaAnimation2.repeatCount = Animation.INFINITE
        alphaAnimation2.repeatMode = Animation.REVERSE
        alarmImage.animation = alphaAnimation2
        alphaAnimation2.start()
    }

    private fun tipRequestPermission() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("请求权限")
            .setMessage("请求打开悬浮窗权限")
            .setPositiveButton("去打开") { _, _ ->
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                context.startActivity(intent)
            }.create().show()
    }


    private fun buildLayoutParams(
        params: WindowManager.LayoutParams,
        targetWidth: Int,
        targetHeight: Int
    ): WindowManager.LayoutParams {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        params.apply {
            width = targetWidth
            height = targetHeight
            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        }
        return params
    }

    private fun calculateWithAndHeight(windowManager: WindowManager): Array<Int> {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        return if (size.x < size.y) {
            arrayOf(size.x, size.y)
        } else {
            arrayOf(size.y, size.x)
        }
    }

}