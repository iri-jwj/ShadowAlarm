package com.android.deskclock.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.net.toUri
import com.android.deskclock.R
import java.io.File

class AlarmNotifyUtil(private val context: Context, private val audioPath: String) {
    private lateinit var player: MediaPlayer
    private lateinit var vibration: Vibrator
    private var action = 0
    private var isNotify = false

    fun notifyAudioAndVibrate(action: Int) {
        this.action = action
        if (!isNotify) {
            if (action.and(0b01) != 0) {
                notifyAudio()
            }

            if (action.and(0b10) != 0) {
                vibration = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibration.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(200, 400, 200, 400, 200, 400),
                            0
                        )
                    )
                    Log.d("AlarmNotify","vibration start")
                } else {
                    vibration.vibrate(longArrayOf(200, 400, 200, 400, 200, 400), 0)
                    Log.d("AlarmNotify","vibration start")
                }
            }
            isNotify = true
        }
    }

    private fun notifyAudio() {
        player = MediaPlayer()
        val file = File(audioPath)
        if (file.exists()) {
            player.setDataSource(file.toUri().toString())
        } else {
            player.setDataSource(
                context,
                Uri.parse("android.resource://${context.packageName}/${R.raw.mlbq}")
            )
        }
        player.isLooping = true
        player.prepare()
        player.start()
        Log.d("AlarmNotify","audio start")
    }

    fun stopNotify() {
        if (action.and(0b01) != 0) {
            player.stop()
            player.reset()
            player.release()
        }
        if (action.and(0b10) != 0) {
            vibration.cancel()
        }
    }
}