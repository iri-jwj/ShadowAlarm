package com.android.deskclock.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.android.deskclock.R

class OverlayWindowUtil(
    private val context: Context, private val title: String,
    private val message: String
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

    fun showFloatingView() {
        if (Settings.canDrawOverlays(context)) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.overlay_window_alarm, null)
            val windowManager =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)

            view.apply {
                findViewById<TextView>(R.id.overlay_label).text = message
                findViewById<TextView>(R.id.overlay_title).text = title

                findViewById<Button>(R.id.overlay_positive).setOnClickListener {
                    onPositiveClicked.invoke()
                    windowManager.removeView(view)
                }

                findViewById<Button>(R.id.overlay_negative).setOnClickListener {
                    onNegativeClicked.invoke()
                    windowManager.removeView(view)
                }
            }
            val xyList = calculateWithAndHeight(windowManager)
            val width = xyList[0]*0.68
            val height = xyList[1]*0.32
            val layoutParams = buildLayoutParams(WindowManager.LayoutParams(),width.toInt(),height.toInt())

            windowManager.addView(
                view,
                layoutParams
            )
        } else {
            tipRequestPermission()
        }
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


    private fun buildLayoutParams(params: WindowManager.LayoutParams,targetWidth:Int,targetHeight:Int): WindowManager.LayoutParams {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        params.apply {
            width =targetWidth
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