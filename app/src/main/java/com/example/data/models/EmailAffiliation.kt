package com.example.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmailAffiliation(
        val email: String,
        val affiliation: String?
) : Parcelable