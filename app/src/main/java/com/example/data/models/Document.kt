package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Document(
        val id: String,
        val name: String
) : Parcelable