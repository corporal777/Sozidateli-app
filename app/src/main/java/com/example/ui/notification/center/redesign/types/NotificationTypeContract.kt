package com.example.ui.notification.center.redesign.types

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface NotificationTypeContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setToolbarTitle(titleRes: Int)
    }

    interface Presenter : BaseContract.Presenter {

    }
}