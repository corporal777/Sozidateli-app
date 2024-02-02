package com.example.data.models

import android.os.Parcelable
import com.example.util.SN_GU
import com.example.util.SN_VK
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SnAuth(
        val token: String,
        val uuid : String,
        val snType: SnType
) : Parcelable

enum class SnType(val code: String) {
        VK(SN_VK), GU(SN_GU)
}

@Parcelize
data class SnUser(
        val snAuth: SnAuth,
        val snUserData: SnUserData?
) : Parcelable

@Parcelize
data class SnUserData(
        val name: String,
        val lastName: String,
        val email: String?,
        val phone: String?,
        val birthday: String?,
        val photo : String?,
        val gender : String?
) : Parcelable