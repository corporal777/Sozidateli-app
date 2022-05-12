package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class News(
        val id: Int,
        val title: String?,
        val text: String?,
        val picture: String?,
        val public_date: String?,
        val event_id:Int
) : Parcelable