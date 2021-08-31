package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
        val id: Int,
        val title: String?,
        val message: String?,
        val date: String,
        val type: Type,
        var wasRead: Boolean,
        var acceptState: AcceptState = AcceptState.NONE,
        val rateId: String? = null,
        @SerializedName("event_id")
        val eventId: Int?,
        @SerializedName("event_activity_id")
        val eventActivityId: Int?,
        val eventInfo: NotificationEventInfo?,
        val event: /*Event*/NotificationEntityModell?,
        val project_name: String?,
        val notificationMainType: String,
        val entity: NotificationEntity?
        ) : Parcelable {

    enum class Type {
        SIMPLE, ACCEPTABLE, RATE
    }

    enum class AcceptState {
        NONE, ACCEPTED, CANCELED, DISABLED
    }

    companion object {
        fun fromRemoteNotification(remoteNotification: /*RemoteNotification*/NotificationModel): Notification {
            val state = when (remoteNotification.entity?.type) {
                "invitePgfr" -> (remoteNotification.entity.model?.state as String)
                "organizationMember" -> (remoteNotification.entity.model?.status as String)
                else -> null
            }
            return Notification(
                    remoteNotification.id?: 0,
                    remoteNotification.entity?.type?:"",
                    remoteNotification.message,
                    remoteNotification.createdDate?: "",
                    when (remoteNotification.entity?.type) {
                        NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR, NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE,
                        NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER, NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> Type.ACCEPTABLE
                        else -> Type.SIMPLE
                    },
                    remoteNotification.acknowledged,
                    when (state) {
                        "confirmed", "approved" -> AcceptState.ACCEPTED
                        "declined" -> AcceptState.CANCELED
                        "cancelled" -> AcceptState.DISABLED
                        else -> AcceptState.NONE
                    },
                    "",
                    if (remoteNotification.entity?.type == NotificationModel.NOTIFICATION_TYPE_EVENT)
                        remoteNotification.entity.model?.id
                    else if (remoteNotification.entity?.type == NotificationModel.NOTIFICATION_TYPE_EVENT_ACTIVITY)
                        remoteNotification.entity.model?.event else 0,
                    if (remoteNotification.entity?.type == NotificationModel.NOTIFICATION_TYPE_EVENT_ACTIVITY)
                        remoteNotification.entity.model?.event else 0,
                    if (remoteNotification.entity?.type == NotificationModel.NOTIFICATION_TYPE_EVENT)
                        NotificationEventInfo(remoteNotification.entity.model?.name, "") else null,
                    NotificationEntityModell(remoteNotification.entity?.model?.id, remoteNotification.entity?.model?.createdDate,
                            remoteNotification.entity?.model?.name, remoteNotification.entity?.model?.createdBy,
                            remoteNotification.entity?.model?.event, remoteNotification.entity?.model?.title,
                            remoteNotification.entity?.model?.description, remoteNotification.entity?.model?.holdingDate,
                            if (remoteNotification.entity?.type == "organizationMember") remoteNotification.entity.model?.status as String
                            else null
                            /*remoteNotification.entity?.model?.status*/,
                            state)
                    /*remoteNotification.entity?.model*/,
                    remoteNotification.entity?.model?.project?.name,
                    remoteNotification.entity?.type?: NotificationModel.NOTIFICATION_TYPE_EVENT,
                    NotificationEntity(remoteNotification.entity?.type, remoteNotification.entity?.id)
            )
            /*return Notification(
                    remoteNotification.id,
                    remoteNotification.type,
                    remoteNotification.text,
                    remoteNotification.time,
                    when (remoteNotification.type) {
                        RemoteNotification.TYPE_RATE -> Type.RATE
                        RemoteNotification.TYPE_INVITE -> Type.ACCEPTABLE
                        else -> Type.SIMPLE
                    },
                    remoteNotification.status != RemoteNotification.STATUS_NONE,
                    when (remoteNotification.status) {
                        RemoteNotification.STATUS_ACKNOWLEDGED,
                        RemoteNotification.STATUS_NONE -> AcceptState.NONE
                        RemoteNotification.STATUS_ACCEPTED -> AcceptState.ACCEPTED
                        RemoteNotification.STATUS_DECLINED -> AcceptState.CANCELED
                        RemoteNotification.STATUS_CANCELLED -> AcceptState.DISABLED
                        else -> AcceptState.NONE
                    },
                    if (remoteNotification.event_id == 0) null else remoteNotification.event_id.toString(),
                    remoteNotification.event_id,
                    remoteNotification.event_activity_id,
                    if (remoteNotification.event == null) null else NotificationEventInfo(remoteNotification.event.name, remoteNotification.event.link),
                    remoteNotification.event, remoteNotification.project_name
            )*/
        }
    }
}

@Parcelize
data class NotificationEntityModell(
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
): Parcelable

@Parcelize
data class NotificationEntity(
        val type: String?,
        val id: Int?
): Parcelable

@Parcelize
data class NotificationEventInfo(
        val name: String?,
        val link: String?
): Parcelable