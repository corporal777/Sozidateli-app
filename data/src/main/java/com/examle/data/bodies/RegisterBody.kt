package com.examle.data.bodies

import com.examle.data.models.UserState
import com.examle.data.models.FieldDetails
import com.examle.data.models.FieldListDetails
import com.examle.data.models.NewUserAddress
import com.examle.data.models.ToggleStringModel
import com.google.gson.annotations.SerializedName

data class RegisterBody(
        val password: String? = null,
        val name: String? = null,
        @SerializedName("lastName")
        val lastName: String? = null,
        @SerializedName("middleName")
        val middleName: FieldDetails? = null,
        val email: FieldDetails? = null,
        val phone: List<FieldDetails>? = null,
        val site: FieldListDetails? = null,
        @SerializedName("socialLinks")
        val socialLinks: FieldListDetails? = null,
        val birthday: FieldDetails? = null,
        val gender: String? = null,
        val address: NewUserAddress? = null,
        val state: UserState? = null,
        val interests: List<Int>? = null,
        val notes: String? = null,
        @SerializedName("educationLevel")
        val educationLevel: Int? = null,
        val deviceId : String = "",
        val deviceModel : String = "",
        var build : String = "",
        var version : String = "",
        var tempToken : String = ""
)

data class SnRegisterBody(
        val name: String? = null,
        val lastName: String? = null,
        val middleName: MiddleNameBody? = null,
        val email: String? = null,
        val phone: String? = null,
        val birthday: String? = null,
        val photo : String? = null,
        val socialNetwork: String? = null,
        val gender : ToggleStringModel? = null,
        val uuid: String? = null,
        val deviceId : String = "",
        val deviceModel : String = "",
        val build : String = "",
        val version : String = ""
)