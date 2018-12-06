package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class News(
        val id: String,
        val title: String,
        val text: String,
        val image: String,
        val date: Long
) : Parcelable