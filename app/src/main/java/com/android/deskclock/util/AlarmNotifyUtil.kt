package com.android.deskclock.util

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.android.deskclock.R

class AlarmNotifyUtil(private val context: Context) {
    private lateinit var player: MediaPlayer
    private lateinit var vibration: Vibrator

    fun notifyAudioAndVibrate(action: Int) {
        if (action.and(0b01) != 0) {
            player = MediaPlayer.create(context, R.raw.mlbq)
            player.isLooping = true
            player.start()
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

    fun stopNotify() {
        player.stop()
        player.release()
        vibration.cancel()
    }
}