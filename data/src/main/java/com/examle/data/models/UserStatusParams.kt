package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class UserStatusParams(
        @SerializedName("user_status_phone")
        val statusPhone: UserStatusParameter,
        @SerializedName("user_status_phone_confirmed")
        val statusPhoneConfirmed: UserStatusParameter,
        @SerializedName("user_fio")
        val name: UserStatusParameter,
        @SerializedName("user_birthday")
        val birthday: UserStatusParameter,
        @SerializedName("user_social_links")
        val socialLinks: UserStatusParameter,
        @SerializedName("user_education")
        val education: UserStatusParameter,
        @SerializedName("user_work")
        val work: UserStatusParameter,
        @SerializedName("user_notes")
        val notes: UserStatusParameter
)