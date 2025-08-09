package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class UserStatusDetails(
        @SerializedName("ALL_PARAMS")
        val params: UserStatusParams,
        @SerializedName("PARAMS_REQUIRE4STATUSES")
        val requires: UserStatusProtectionRequires
)