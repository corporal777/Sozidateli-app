package com.example.ui.notification.center

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract

interface NotificationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(notifications: List<Notification?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun onNotificationNeedUpdate(id: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRating(eventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showNotification(notification: Notification)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(eventId: String)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun onNotificationUrlClick(url: String)
        fun onItemTake(position: Int)
        fun onRefreshRequest()

        fun onNotificationReadMoreClick(id: Int)
        fun onNotificationReadClick(id: Int)
        fun onNotificationAcceptClick(notification: Notification)
        fun onNotificationCancelClick(notification: Notification)
        fun onNotificationChangeDecisionClick(notification: Notification)
        fun onNotificationRateClick(eventId: String)
    }
}
