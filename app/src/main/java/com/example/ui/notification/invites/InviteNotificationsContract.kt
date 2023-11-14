package com.example.ui.notification.invites

import com.example.data.models.Notification
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.ui.notification.NotificationsSortedData
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface InviteNotificationsContract {
    interface View : BaseBottomSheetContract.View{

        @AddToEndSingle
        fun setUnreadInvitesLabel(invites : Int)

        @AddToEndSingle
        fun setNotifications(notifications: List<NotificationsSortedData>)

        @OneExecution
        fun showAboutEvent(eventId : String)

        @OneExecution
        fun showAboutOrganization(organizationId : String)

        @OneExecution
        fun showUrl(url: String)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onNotificationAcceptClick(notification: Notification)
        fun onNotificationCancelClick(notification: Notification)

        fun onItemTake(position: Int)
    }
}