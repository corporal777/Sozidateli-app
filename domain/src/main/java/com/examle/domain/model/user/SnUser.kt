package com.examle.domain.model.user

import android.os.Parcelable
import com.examle.domain.model.auth.SnAuthModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SnUser(
    val snAuth: SnAuthModel,
    val snUserData: SnUserData?
) : Parcelable

@Parcelize
data class SnUserData(
    val name: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val birthday: String?,
    val photo : String?,
    val gender : String?
) : Parcelable