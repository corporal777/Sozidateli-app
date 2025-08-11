package com.examle.domain.model.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthModel(
    val id: Int,
    val token: String
) : Parcelable
