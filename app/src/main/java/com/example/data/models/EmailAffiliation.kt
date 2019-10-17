package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class EmailAffiliation(
        val email: String,
        val affiliation: String?
) : Parcelable