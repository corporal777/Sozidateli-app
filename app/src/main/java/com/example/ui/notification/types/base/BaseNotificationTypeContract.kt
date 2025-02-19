package com.example.ui.notification.types.base

import androidx.paging.PagingData
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.ui.base.BaseContract
import com.example.ui.notification.NotificationType
import com.example.ui.notification.NotificationsSortedData
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface BaseNotificationTypeContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setNotifications(notifications: PagingData<NotificationLocal>)

        @OneExecution
        fun showAboutEvent(eventId: String?)

        @OneExecution
        fun showAboutOrganization(organizationId: String?)

        @Skip
        fun setReadAllButton(show: Boolean)

        @Skip
        fun showInvitesBottomSheet(type: NotificationType)

        @Skip
        fun updateNotification(notificationId : Int)

        @Skip
        fun updateNotification(data : NotificationLocal)

        @Skip
        fun setAppBarElevation(shadow : Float)
    }

    interface Presenter : BaseContract.Presenter {
        fun onReadAllNotifications(type: NotificationType)
        fun onRefreshRequest()
        fun onNotificationUrlClick(url: String)
        fun changeScrollingElevation(value: Int)
    }
}