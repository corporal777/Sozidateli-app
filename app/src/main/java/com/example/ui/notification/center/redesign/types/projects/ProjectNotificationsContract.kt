package com.example.ui.notification.center.redesign.types.projects

import com.example.data.models.Notification
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypeContract

interface ProjectNotificationsContract {

    interface View : BaseNotificationTypeContract.View {
    }

    interface Presenter : BaseNotificationTypeContract.Presenter {
        fun onNotificationReadClick(id: Int)
        fun onNotificationAcceptClick(notification: Notification)
        fun onNotificationCancelClick(notification: Notification)
    }

}