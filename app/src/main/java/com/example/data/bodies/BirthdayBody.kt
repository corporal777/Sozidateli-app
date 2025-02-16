package com.example.data.bodies

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BirthdayBody(
    val value: String? = null,
    val isVisible: Boolean? = false,
) : Parcelable