package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenStatusModel(
    val id: Int
) : Parcelable