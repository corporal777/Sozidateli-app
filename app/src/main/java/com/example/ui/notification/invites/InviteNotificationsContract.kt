package com.example.ui.notification.invites

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.ui.notification.NotificationsSortedData

interface InviteNotificationsContract {
    interface View : BaseBottomSheetContract.View{

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setUnreadInvitesLabel(invites : Int)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setNotifications(notifications: List<NotificationsSortedData>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(eventId : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutOrganization(organizationId : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onNotificationAcceptClick(notification: Notification)
        fun onNotificationCancelClick(notification: Notification)

        fun onItemTake(position: Int)
    }
}