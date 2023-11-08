package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SupportData(
    val id : Int,
    val header : String,
    val question : String,
    val answer : String,
    val type : String
): Parcelable