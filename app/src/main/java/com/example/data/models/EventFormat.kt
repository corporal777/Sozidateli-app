package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventFormat(
        val id: Int,
        val name: String
) : Parcelable