package com.example.ui.state.maxNew.base

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.ui.state.maxNew.MaxStateScreenType

interface BaseMaxStateContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun goToNextScreen(screenType: MaxStateScreenType)

        @StateStrategyType(SkipStrategy::class)
        fun showMaxStateDone(screen: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setClickClose(type : Int)

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun buttonNextEnabled(enabled: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showAddEmailDialog()

        @StateStrategyType(SkipStrategy::class)
        fun hideAddEmailDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showEmailIsNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailConfirmation(email: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowMaxStateDone()
        fun onClickClose()

        fun checkEmailIsUnique(email: String)
        fun onShowEmailConfirm(email : String)

        fun checkUserEmail()
    }
}