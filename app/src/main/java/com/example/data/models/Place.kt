package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
        val coordinates: List<Double>,
        val howToGet: String,
        val schemeImage: String,
        val scheme: String
) : Parcelable