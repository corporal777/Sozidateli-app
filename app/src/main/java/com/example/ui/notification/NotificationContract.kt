package com.example.ui.notification

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.InviteDetail
import com.example.data.models.Notification
import com.example.ui.base.BaseContract

interface NotificationContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(notification: Notification)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRating(eventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showErrorDialog(errors: List<String>, projectName: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSuccessAccepted()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSuccessCanceled()
    }

    interface Presenter : BaseContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onNotificationAcceptClick()
        fun onNotificationCancelClick()
        fun onNotificationChangeDecisionClick()
        fun onNotificationRateClick()
    }
}
