package com.example.ui.notification.types.system

import com.example.ui.notification.types.base.BaseNotificationTypeContract

interface SystemNotificationsContract {
    interface View : BaseNotificationTypeContract.View {
    }

    interface Presenter : BaseNotificationTypeContract.Presenter {
        fun onNotificationReadClick(id: Int)
    }
}