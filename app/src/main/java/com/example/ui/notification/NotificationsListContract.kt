package com.example.ui.notification

import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface NotificationsListContract {
    interface View : BaseContract.View {

        @AddToEndSingle
        fun setNotificationsPlaceholder()

        @AddToEndSingle
        fun setData(notifications: List<NotificationsSortedData>)

        @AddToEndSingle
        fun showEmptyListPlaceholder()

        @OneExecution
        fun showUrl(url: String)

        @OneExecution
        fun onNotificationNeedUpdate(data : Notification)

        @OneExecution
        fun showAboutEvent(eventId: String)

        @OneExecution
        fun showAboutOrganization(id: String?)

        @OneExecution
        fun setNotReadButtonEnabled(enabled: Boolean)

        @Skip
        fun showInvitesBottomSheet()
    }

    interface Presenter : BaseContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onItemTake(position: Int)
        fun onRefreshRequest()
        fun onNotificationReadClick(id: Int)
        fun onNotificationAcceptClick(notification: Notification)
        fun onNotificationCancelClick(notification: Notification)
        fun onNotificationRateClick(eventId: String)
        fun onReadAllNotificationsClick()
    }
}