package com.android.deskclock.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class ShadowAlarm(
    val id: UUID,
    var label: String,
    var remindHours: Int,
    var remindMinutes: Int,
    var remindDaysInWeek: Int,
    var remindAction: Int,
    var isEnabled: Boolean
) : Cloneable, Parcelable {

    constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt() != 0
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        val enabled = if (isEnabled) {
            1
        } else {
            0
        }
        dest?.apply {
            writeString(id.toString())
            writeString(label)
            writeInt(remindHours)
            writeInt(remindMinutes)
            writeInt(remindDaysInWeek)
            writeInt(remindAction)
            writeInt(enabled)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShadowAlarm> {
        override fun createFromParcel(parcel: Parcel): ShadowAlarm {
            return ShadowAlarm(parcel)
        }

        override fun newArray(size: Int): Array<ShadowAlarm?> {
            return arrayOfNulls(size)
        }
    }

    override fun clone(): ShadowAlarm {
        val newId = UUID.fromString(id.toString())
        val newLabel = String(label.toCharArray())
        return ShadowAlarm(
            newId,
            newLabel,
            remindHours,
            remindMinutes,
            remindDaysInWeek,
            remindAction,
            isEnabled
        )
    }

    fun getNewCopy(): ShadowAlarm {
        return clone()
    }
}