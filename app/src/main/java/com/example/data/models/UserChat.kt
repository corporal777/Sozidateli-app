package com.example.data.models

import android.os.Parcelable
import com.example.data.models.user.User
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import ru.houseofapps.chat.models.Message

@Parcelize
data class UserChat(
        val id: Int,
        @SerializedName("user_recepient")
        var user: User,
        val created: String,
        @SerializedName("last_message")
        val lastMessage: String?,
        @SerializedName("last_message_datetime")
        val lastMessageDate: String?,
        @SerializedName("last_message_type")
        val lastMessageType: Message.Type?,
        @SerializedName("last_message_user_id")
        val lastMessageSender: Int?,
        @SerializedName("user_sender")
        var userSender: User?,
        @SerializedName("last_message_id")
        var messageId: String?,
        @SerializedName("user_recepient_in_favorite")
        var inFavorite: Boolean
) : Parcelable