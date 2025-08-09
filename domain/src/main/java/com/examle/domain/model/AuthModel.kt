package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class AuthModel(
    val id: Int,
    val token: String
) : Parcelable
