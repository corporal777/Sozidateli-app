package com.example.ui.notification

import androidx.paging.PagingData
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface NotificationsListContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setData(notifications: PagingData<NotificationLocal>)

        @Skip
        fun showUrl(url: String)

        @OneExecution
        fun updateNotification(data : NotificationLocal?, notificationId : Int)

        @OneExecution
        fun showAboutEvent(eventId: String)

        @OneExecution
        fun showAboutOrganization(id: String?)

        @Skip
        fun setNotReadButtonEnabled(enabled: Boolean)

        @Skip
        fun showInvitesBottomSheet()
    }

    interface Presenter : BaseContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onRefreshRequest()
        fun onNotificationReadClick(id: Int)
        fun onNotificationAcceptClick(notification: NotificationLocal)
        fun onNotificationCancelClick(notification: NotificationLocal)
        fun onNotificationRateClick(eventId: String)
        fun onReadAllNotificationsClick()
    }
}