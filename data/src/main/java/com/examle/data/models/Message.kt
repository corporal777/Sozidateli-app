package com.examle.data.models

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class Message(
        val _id: String,
        val type: MessageType,
        val roomKey: String,
        val message: String,
        val senderKey: String,
        val createdAt: Long = 0,
        val updatedAt: Long = 0,
        val forUser: String? = null,
        var wasRead: Boolean = false,
        val additionalData: JsonElement? = null
) {

    fun isUserMessage(id: String): Boolean {
        return id == senderKey
    }

    enum class MessageType(val value: String) {
        @SerializedName("text")
        TEXT("text"),
        @SerializedName("image")
        IMAGE("image"),
        @SerializedName("audio")
        AUDIO("audio"),
        @SerializedName("video")
        VIDEO("video"),
        @SerializedName("service")
        SERVICE("service")
    }
}