package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
        val id: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("organization_id")
        val organizationId: Int?,
        @SerializedName("project_id")
        val projectId: Int?,
        val code: Int?,
        val type: String,
        val text: String,
        val status: String,
        val extra: String,
        val time: String
):Parcelable