package com.example.ui.snAuth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SnAuth(
        val token: String,
        val email: String? = null,
        val snType: SnType
) : Parcelable