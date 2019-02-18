package com.example.data.models

import com.example.util.FIELD_IMAGE
import com.example.util.FIELD_SENDER_ID
import com.example.util.FIELD_SEND_AT
import com.example.util.FIELD_TEXT
import com.google.firebase.firestore.FieldValue
import java.util.*

data class ChatMessage(
        var id: String? = null,
        val text: String? = null,
        val senderId: Int = -1,
        val sendAt: Date = Date(),
        var isRead: Boolean? = false,
        var image: String? = null
) {
    fun toMap() = mapOf(
            FIELD_TEXT to text,
            FIELD_SENDER_ID to senderId,
            FIELD_SEND_AT to FieldValue.serverTimestamp(),
            FIELD_IMAGE to image
    )
}