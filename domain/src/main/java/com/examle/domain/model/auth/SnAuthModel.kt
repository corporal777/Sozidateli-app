package com.examle.domain.model.auth

import android.os.Parcelable
import com.example.common.constants.SN_GU
import com.example.common.constants.SN_VK
import kotlinx.parcelize.Parcelize

@Parcelize
data class SnAuthModel(
        val token: String,
        val uuid : String,
        val snType: SnType
) : Parcelable

enum class SnType(val code: String) {
        VK(SN_VK), GU(SN_GU)
}

