package com.example.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapInfo(
        val lat: Double?,
        val lon: Double?,
        val title: String?,
        val description: String?
) : Parcelable