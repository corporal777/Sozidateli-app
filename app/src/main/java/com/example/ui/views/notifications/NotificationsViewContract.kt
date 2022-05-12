package com.example.ui.views.notifications

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

class NotificationsViewContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun showCounter(show: Boolean)
    }

    interface Presenter : BaseContract.Presenter
}