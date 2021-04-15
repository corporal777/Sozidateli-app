package com.example.data.models

import com.google.gson.annotations.SerializedName

data class NotificationModel (
        val id: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        val user: Int? = null,
        val message: String? = null,
        val entity: NotificationEntityModel? = null
) {
        companion object {
                const val NOTIFICATION_LIMIT = "limit"
                const val NOTIFICATION_OFFSET = "offset"
        }
}

data class NotificationEntityModel(
    val type: String? = null,
    val id: Int? = null,
    val model: EventNew? = null
)