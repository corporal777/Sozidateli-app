package com.example.data.models

import com.google.firebase.firestore.FieldValue
import java.util.*

data class ChatMessage(
        val text: String? = null,
        val senderId: Int = -1,
        val sendAt: Date = Date()
) {
    fun toMap() = mapOf(
            FIELD_TEXT to text,
            FIELD_SENDER_ID to senderId,
            FIELD_SEND_AT to FieldValue.serverTimestamp()
    )

    companion object {
        const val FIELD_TEXT = "text"
        const val FIELD_SENDER_ID = "senderId"
        const val FIELD_SEND_AT = "sendAt"
    }
}