package com.example.ui.notification.center.redesign

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface NotificationsListContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setNotificationsPlaceholder()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(notifications: List<NotificationsSortedData>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class)
        fun showEmptyListPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun onNotificationNeedUpdate(data : Notification)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(eventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutOrganization(id: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setNotReadButtonEnabled(enabled: Boolean)

        @StateStrategyType(SkipStrategy::class)
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