package com.example.data.models

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class NotificationModel(
    val id: Int? = null,
    val partitionType: String? = null,
    @SerializedName("createdDate")
    val createdDate: String? = null,
    val user: Int? = null,
    val message: String? = null,
    val acknowledged: Boolean = false,
    @SerializedName("isInApp")
    val isInApp: Boolean = false,
    val isInvite: Boolean = false,
    val notificationType: String? = null,
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
        const val NOTIFICATION_SORT = "sortType"
        const val NOTIFICATION_SORT_FIELD = "sortField"
        const val NOTIFICATION_ENTITY_TYPE = "entityType"
        const val NOTIFICATION_EVENT_ID = "entityId"
        const val NOTIFICATION_ACKNOWLEDGED = "acknowledged"
        const val NOTIFICATION_IS_IN_APP = "isInApp"

        const val NOTIFICATION_IS_INVITE = "isInvite"
        const val NOTIFICATION_IS_ARCHIVE = "isArchive"
        const val NOTIFICATION_TYPE = "type"
    }
}

data class NotificationEntityModel(
    val type: String? = null,
    val id: Int? = null,
    val state: String,
    val model: NotificationEntityModelModel? = null
)


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
    val status: Any? = null,
    val project: ProjectObject? = null,
    val state: Any? = null,
    val organization: Any? = null
)

@Parcelize
data class ProjectObject(
    val name: String? = null,
    val id: String? = null
) : Parcelable

@Parcelize
data class NotificationStatusModel(
    val value: String? = null,
    val changed: String? = null,
    val comments: String? = null
) : Parcelable

@Parcelize
data class NotificationsTypesModel(
    val system: Int,
    val pgrf: Int,
    val event: Int,
    val evaluate: Int,
    val org: Int
) : Parcelable

@Parcelize
data class NotificationInviteModel(
    val pgrf: Int,
    val event: Int,
    val org: Int
) : Parcelable