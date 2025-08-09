package com.examle.data.models.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Value(
        var id: Int = -1,
        var value: String
) : Parcelable