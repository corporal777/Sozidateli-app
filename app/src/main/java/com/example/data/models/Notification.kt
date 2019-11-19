package com.example.data.models

import android.os.Parcelable
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
        val rateId: Int? = null
) : Parcelable {

    enum class Type {
        SIMPLE, ACCEPTABLE, RATE
    }

    enum class AcceptState {
        NONE, ACCEPTED, CANCELED, DISABLED
    }

    companion object {
        fun fromRemoteNotification(remoteNotification: RemoteNotification): Notification {
            return Notification(
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
                    remoteNotification.event_id
            )
        }
    }
}