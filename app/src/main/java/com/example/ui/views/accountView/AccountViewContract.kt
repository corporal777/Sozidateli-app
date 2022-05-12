package com.example.ui.views.accountView

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

class AccountViewContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun showCounter(show: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setAvatar(url: String?)
    }

    interface Presenter : BaseContract.Presenter {

        var canShowBadge: Boolean
    }
}