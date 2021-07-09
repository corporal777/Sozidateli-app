package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class NotificationModel (
        val id: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        val user: Int? = null,
        val message: String? = null,
        val acknowledged: Boolean = false,
        @SerializedName("isInApp")
        val isInApp: Boolean = false,
        val entity: NotificationEntityModel? = null
) {
        companion object {
                const val NOTIFICATION_LIMIT = "limit"
                const val NOTIFICATION_OFFSET = "offset"
                const val NOTIFICATION_USER = "user"
                const val NOTIFICATION_LOAD_MODEL = "loadModel"
                const val NOTIFICATION_TYPE_INVITE_PGFR = "invitePgfr"
                const val NOTIFICATION_TYPE_INVITE_ASSISTANCE = "inviteAssistance"
                const val NOTIFICATION_TYPE_ORGANIZATION_MEMBER = "organizationMember"
                const val NOTIFICATION_TYPE_EVENT = "event"
                const val NOTIFICATION_TYPE_EVENT_ACTIVITY = "eventActivity"
                const val NOTIFICATION_TYPE_EVENT_MEMBER = "eventMember"
        }
}

data class NotificationEntityModel(
    val type: String? = null,
    val id: Int? = null,
    val model: NotificationEntityModelModel? = null
)

@Parcelize
data class NotificationEntityModelModel(
        val id: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        val name: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val event: Int? = null,
        val title: String? = null,
        val description: String? = null,
        @SerializedName("holdingDate")
        val holdingDate: DateModel? = null,
        val status: NotificationStatusModel/*Event.Status*/? = null
): Parcelable

@Parcelize
data class NotificationStatusModel(
        val value: String? = null,
        val changed: String? = null,
        val comments: String? = null
): Parcelable