package com.examle.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchTypeEvent(
        var id: String,
        var name: String,
        var selected: Boolean = false
) : Parcelable