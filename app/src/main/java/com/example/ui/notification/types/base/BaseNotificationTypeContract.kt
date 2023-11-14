package com.example.ui.notification.types.base

import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import com.example.ui.notification.NotificationType
import com.example.ui.notification.NotificationsSortedData
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface BaseNotificationTypeContract {
    interface View : BaseContract.View {

        @AddToEndSingle
        fun setPlaceholder(notifications: List<Notification?>)

        @AddToEndSingle
        fun setNotifications(notifications: List<NotificationsSortedData>)

        @AddToEndSingle
        fun showEmptyListPlaceholder()

        @OneExecution
        fun showUrl(url: String)

        @OneExecution
        fun showAboutEvent(eventId: String?)

        @OneExecution
        fun showAboutOrganization(organizationId: String?)

        @Skip
        fun setReadAllButton(show: Boolean)

        @Skip
        fun showInvitesBottomSheet(type: NotificationType)

        @OneExecution
        fun onNotificationNeedUpdate(data: Notification)

        @Skip
        fun setAppBarElevation(shadow : Float)
    }

    interface Presenter : BaseContract.Presenter {
        fun onReadAllClick()
        fun onItemTake(position: Int)
        fun getTitleDatesCount(): Int
        fun onRefreshRequest()

        fun onNotificationUrlClick(url: String)

        fun changeScrollingElevation(value: Int)
    }
}