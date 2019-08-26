package com.android.deskclock.model

import android.net.Uri
import java.util.*

data class ShadowAlarm(
    val id: UUID,
    var label: String,
    var remindHours: Int,
    var remindMinutes: Int,
    var remindDaysInWeek: Int,
    var alarmAudio: Uri
)