package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Interest(
        val id: Int,
        val parent: Int,
        val value: String
) : Parcelable