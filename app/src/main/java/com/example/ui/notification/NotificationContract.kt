package com.example.ui.notification

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract

interface NotificationContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(notification: Notification, showButtons: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRatingChooser()
    }

    interface Presenter : BaseContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onNotificationAcceptClick()
        fun onNotificationCancelClick()
        fun onNotificationChangeDecisionClick()
        fun onNotificationRateClick()
        fun onNotificationRatingChosen(rating: Int)
    }
}
