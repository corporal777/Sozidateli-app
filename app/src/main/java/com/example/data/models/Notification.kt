package com.example.data.models

import android.os.Parcelable
import com.example.data.models.NotificationModel.Companion.NOTIFICATION_TYPE_EVENT
import com.example.data.models.NotificationModel.Companion.NOTIFICATION_TYPE_EVENT_ACTIVITY
import com.example.data.models.NotificationModel.Companion.NOTIFICATION_TYPE_EVENT_MEMBER
import com.example.data.models.NotificationModel.Companion.NOTIFICATION_TYPE_INVITE_ASSISTANCE
import com.example.data.models.NotificationModel.Companion.NOTIFICATION_TYPE_INVITE_PGFR
import com.example.data.models.NotificationModel.Companion.NOTIFICATION_TYPE_ORGANIZATION_MEMBER
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: Int,
    val message: String?,
    val date: String,
    val type: Type,
    val partitionType: String,
    var wasRead: Boolean,
    val isInApp: Boolean,
    val isInvite: Boolean,
    var acceptState: AcceptState = AcceptState.NONE,
    val rateId: String? = null,
    @SerializedName("event_id")
    val eventId: Int?,
    @SerializedName("event_activity_id")
    val eventActivityId: Int?,
    val eventInfo: NotificationEventInfo?,
    val event: NotificationEntityLocalModel?,
    val project_name: String?,
    val notificationMainType: String,
    val entity: NotificationEntity?,
    val organization: Int
) : Parcelable {


    enum class Type {
        SIMPLE, ACCEPTABLE, RATE
    }

    enum class AcceptState {
        NONE, ACCEPTED, CANCELED, DISABLED
    }

    companion object {
        fun fromRemoteNotification(remoteNotification: NotificationModel): Notification {
            val notificationMainType =
                 if (!remoteNotification.entity?.type.isNullOrEmpty()) remoteNotification.entity?.type
                 else if (!remoteNotification.notificationType.isNullOrEmpty()) remoteNotification.notificationType
                 else NOTIFICATION_TYPE_EVENT

            val state = when (remoteNotification.entity?.type) {
                NOTIFICATION_TYPE_EVENT_MEMBER -> {
                    if (remoteNotification.entity.model?.status != null) {
                        remoteNotification.entity.model.status as String
                    } else null
                }
                NOTIFICATION_TYPE_INVITE_PGFR -> remoteNotification.entity.state
                NOTIFICATION_TYPE_INVITE_ASSISTANCE -> remoteNotification.entity.state
                NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> remoteNotification.entity.state
                else -> null
            }
            val notificationType = when (remoteNotification.entity?.type) {
                NOTIFICATION_TYPE_INVITE_PGFR,
                NOTIFICATION_TYPE_INVITE_ASSISTANCE,
                NOTIFICATION_TYPE_ORGANIZATION_MEMBER,
                NOTIFICATION_TYPE_EVENT_MEMBER -> Type.ACCEPTABLE
                else -> Type.SIMPLE
            }
            val rateId = remoteNotification.entity?.id.toString()
            val eventId = when (remoteNotification.entity?.type) {
                NOTIFICATION_TYPE_EVENT -> remoteNotification.entity.model?.id
                NOTIFICATION_TYPE_EVENT_ACTIVITY -> remoteNotification.entity.model?.event
                else -> 0
            }

            val eventActivityId =
                if (remoteNotification.entity?.type == NOTIFICATION_TYPE_EVENT_ACTIVITY)
                    remoteNotification.entity.model?.event
                else 0

            val eventInfo =
                if (remoteNotification.entity?.type == NOTIFICATION_TYPE_EVENT)
                    NotificationEventInfo(remoteNotification.entity.model?.name, "")
                else null

            val entityModel = NotificationEntityLocalModel(
                remoteNotification.entity?.model?.id,
                remoteNotification.entity?.model?.createdDate,
                remoteNotification.entity?.model?.name,
                remoteNotification.entity?.model?.createdBy,
                remoteNotification.entity?.model?.event,
                remoteNotification.entity?.model?.title,
                remoteNotification.entity?.model?.description,
                remoteNotification.entity?.model?.holdingDate,
                if (remoteNotification.entity?.type == "organizationMember") remoteNotification.entity.model?.status as String
                else null,
                state
            )

            val organization =
                if (remoteNotification.entity?.type == "invitePgfr") 0
                else {
                    when (remoteNotification.entity?.model?.organization) {
                        is Int -> remoteNotification.entity.model.organization
                        is Double -> remoteNotification.entity.model.organization.toInt()
                        else -> 0
                    }
                }

            return Notification(
                remoteNotification.id ?: 0,
                remoteNotification.message,
                remoteNotification.createdDate ?: "",
                notificationType,
                remoteNotification.partitionType ?: "",
                remoteNotification.acknowledged,
                remoteNotification.isInApp,
                remoteNotification.isInvite,
                when (state) {
                    "confirmed", "approved", "accepted" -> AcceptState.ACCEPTED
                    "declined" -> AcceptState.CANCELED
                    "canceled", "cancelled" -> AcceptState.DISABLED
                    else -> AcceptState.NONE
                },
                rateId,
                eventId,
                eventActivityId,
                eventInfo,
                entityModel,
                remoteNotification.entity?.model?.project?.name,
                notificationMainType ?: NOTIFICATION_TYPE_EVENT,
                NotificationEntity(remoteNotification.entity?.type, remoteNotification.entity?.id),
                organization
            )
        }
    }
}

@Parcelize
data class NotificationEntityLocalModel(
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
    val status: String? = null,
    val state: String? = null
) : Parcelable

@Parcelize
data class NotificationEntity(
    val type: String?,
    val id: Int?
) : Parcelable

@Parcelize
data class NotificationEventInfo(
    val name: String?,
    val link: String?
) : Parcelable

@Parcelize
data class UnacceptedInviteNotification(
    @SerializedName("unacceptedInvitations")
    val unAcceptedInvites: Int
) : Parcelable

