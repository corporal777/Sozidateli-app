package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Partner(
        var id:Int,
        var logo:String?,
        var name:String?,
        var description:String?
):Parcelable