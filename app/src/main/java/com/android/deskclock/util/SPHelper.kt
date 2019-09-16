package com.android.deskclock.util

import android.content.Context
import android.content.SharedPreferences

object SPHelper {
    private const val spName = "ShutDownTime"
    private const val key = "targetTime"
    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

    fun saveNewShutDownTime(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.putLong(key, System.currentTimeMillis())
        editor.apply()
    }

    fun getShutDownTime(context: Context): Long {
        val sp = getSharedPreference(context)
        return if (sp.contains(key)) {
            sp.getLong(key, 0L)
        } else {
            0L
        }
    }
}