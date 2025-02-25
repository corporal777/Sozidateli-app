package com.example.ui.notification.types.evaluate

import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.ui.notification.types.base.BaseNotificationTypeContract

interface EvaluateNotificationsContract {

    interface View : BaseNotificationTypeContract.View {
    }

    interface Presenter : BaseNotificationTypeContract.Presenter {
        fun onNotificationReadClick(id: Int)
        fun onNotificationAcceptClick(notification: NotificationLocal)
        fun onNotificationCancelClick(notification: NotificationLocal)
    }
}