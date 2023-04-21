package com.example.ui.notification.center.redesign.invites

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface InviteNotificationsBottomSheetContract {
    interface View : BaseBottomSheetContract.View{

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setUnreadInvitesLabel(invites : Int)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setNotifications(notifications: Map<String, List<Notification>>)

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