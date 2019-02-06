package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataArgsSearchType(
        var array: MutableList<SearchTypeEvent>,
        var isPlaces: Boolean
) : Parcelable