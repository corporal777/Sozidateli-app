package com.example.data.bodies

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FieldPhoneBody(
    val value: String? = null,
    val type: String? = null,
    @SerializedName("isVisible")
    val isVisible: Boolean? = true,
    val absent: Boolean? = false,
    val additional: String? = null
) : Parcelable {

    fun toList(): ArrayList<FieldPhoneBody> {
        return arrayListOf(this)
    }
}