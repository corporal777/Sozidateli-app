package com.example.ui.notification.center

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface NotificationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setNotificationsList()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(notifications: List<Notification?>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class)
        fun showEmptyListPlaceholder()

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

    interface Presenter : BaseContract.Presenter {
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
