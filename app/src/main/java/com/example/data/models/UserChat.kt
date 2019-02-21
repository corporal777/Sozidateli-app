package com.example.data.models

import android.os.Parcelable
import com.example.data.models.user.User
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserChat(
        val id: String,
        @SerializedName("user_recepient")
        var user: User,
        val created: String,
        @SerializedName("last_message")
        val lastMessage: String?,
        @SerializedName("last_message_datetime")
        val lastMessageDate: String?,
        @SerializedName("last_message_user_id")
        val lastMessageSender: Int?
) : Parcelable