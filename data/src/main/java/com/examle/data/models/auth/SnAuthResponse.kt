package com.examle.data.models.auth

import com.examle.domain.model.user.SnUserData

data class SnAuthResponse(
    val accessData: AuthResponse?,
    val personalData: SnUserData?
)