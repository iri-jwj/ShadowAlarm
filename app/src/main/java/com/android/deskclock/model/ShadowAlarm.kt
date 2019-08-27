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
    var isEnabled: Boolean
):Parcelable{

    constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        val enabled = if (isEnabled){
            1
        }else{
            0
        }
        dest?.apply {
            writeString(id.toString())
            writeString(label)
            writeInt(remindHours)
            writeInt(remindMinutes)
            writeInt(remindDaysInWeek)
            writeByte(enabled.toByte())
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


}