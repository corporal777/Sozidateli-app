package com.example.data.models

import android.os.Parcelable
import com.example.ui.snAuth.SnAuth
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SnUser(
        val snAuth: SnAuth,
        val snUserData: SnUserData
) : Parcelable