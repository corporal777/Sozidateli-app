package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SnUserData(
        val id: String,
        val firstName: String?,
        val lastName: String?,
        val avatar: String?,
        val email: String?
) : Parcelable