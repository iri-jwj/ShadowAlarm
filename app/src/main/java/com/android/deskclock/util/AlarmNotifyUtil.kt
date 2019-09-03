package com.android.deskclock.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.android.deskclock.R
import java.io.File

class AlarmNotifyUtil(private val context: Context, private val audioPath: String) {
    private lateinit var player: MediaPlayer
    private lateinit var vibration: Vibrator

    fun notifyAudioAndVibrate(action: Int) {
        if (action.and(0b01) != 0) {
            notifyAudio()
        }

        if (action.and(0b10) != 0) {
            vibration = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibration.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(100, 200, 100, 200),
                        0
                    )
                )
            } else {
                vibration.vibrate(longArrayOf(100, 200, 100, 200), 0)
            }
        }

    }

    private fun notifyAudio() {
        player = MediaPlayer()
        val file = File(audioPath)
        if (file.exists()) {
            player.setDataSource(audioPath)
        } else {
            player.setDataSource(
                context,
                Uri.parse("android.resource://${context.packageName}/${R.raw.mlbq}")
            )
        }
        player.isLooping = true
        player.prepare()
        player.start()
    }

    fun stopNotify() {
        player.stop()
        player.reset()
        player.release()
        vibration.cancel()
    }
}