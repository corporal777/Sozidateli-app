package com.example.ui.notification.center.redesign

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface NotificationsListContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(notifications: List<Notification?>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setDataNew(notifications: Map<String, List<Notification>>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class)
        fun showEmptyListPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun onNotificationNeedUpdate(id: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(eventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setNotReadButtonEnabled(enabled: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onItemTake(position: Int)
        fun onRefreshRequest()
        fun onNotificationReadClick(id: Int)
        fun onNotificationAcceptClick(notification: Notification)
        fun onNotificationCancelClick(notification: Notification)
        fun onNotificationRateClick(eventId: String)
        fun showOnlyNotRead(show: Boolean)
        fun onReadAllNotificationsClick()
    }
}