package com.example.data.models.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DefaultDate(
        var date: String? = null,
        var timezone_type: Int = -1,
        var timezone: String? = null
): Parcelable