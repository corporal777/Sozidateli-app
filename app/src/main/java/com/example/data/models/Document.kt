package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Document(
        var id: Int,
        var event_id: Int,
        var public_date:String?,
        var mime: String?,
        var description:String?,
        var file:String?
) : Parcelable