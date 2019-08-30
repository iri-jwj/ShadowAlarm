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

    fun notifyAudioAndVibrate() {
        player = MediaPlayer.create(context, R.raw.mlbq)
        player.isLooping = true
        player.start()

        vibration = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibration.vibrate(VibrationEffect.createWaveform(longArrayOf(10, 100, 10, 100), 0))
        } else {
            vibration.vibrate(longArrayOf(10, 100, 10, 100), 0)
        }
    }

    fun stopNotify() {
        player.stop()
        player.release()
        vibration.cancel()
    }
}