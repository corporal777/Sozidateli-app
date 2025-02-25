package com.example.ui.notification.invites

import androidx.paging.PagingData
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.ui.base.bottomSheet.BaseBSContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface InviteNotificationsContract {
    interface View : BaseBSContract.View{
        @Skip
        fun setUnreadInvitesLabel(invites : Int)

        @OneExecution
        fun setNotifications(notifications: PagingData<NotificationLocal>)

        @Skip
        fun updateNotification(data : NotificationLocal?, notificationId : Int)

        @OneExecution
        fun showAboutEvent(eventId : String)

        @OneExecution
        fun showAboutOrganization(organizationId : String)

        @OneExecution
        fun showUrl(url: String)
    }

    interface Presenter : BaseBSContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onNotificationAcceptClick(notification: NotificationLocal)
        fun onNotificationCancelClick(notification: NotificationLocal)

    }
}