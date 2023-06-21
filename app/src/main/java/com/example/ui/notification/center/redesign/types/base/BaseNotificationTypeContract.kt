package com.example.ui.notification.center.redesign.types.base

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.NotificationsSortedData
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.util.AddToEndSingleByTagStateStrategy

interface BaseNotificationTypeContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPlaceholder(notifications: List<Notification?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setNotifications(notifications: List<NotificationsSortedData>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmptyListPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(eventId: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutOrganization(organizationId: String?)

        @StateStrategyType(SkipStrategy::class)
        fun setReadAllButton(show: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showInvitesBottomSheet(type: NotificationType)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun onNotificationNeedUpdate(data: Notification)

        @StateStrategyType(SkipStrategy::class)
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