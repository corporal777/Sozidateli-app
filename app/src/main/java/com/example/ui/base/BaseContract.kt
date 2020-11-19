package com.example.ui.base

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BaseContract {
    interface View : MvpView, LoadingView {
        @StateStrategyType(SkipStrategy::class)
        fun showToast(@StringRes message: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showToast(message: String)

        @StateStrategyType(SkipStrategy::class)
        fun navigateUp()

        @StateStrategyType(SkipStrategy::class)
        fun hideKeyboard()

        @StateStrategyType(SkipStrategy::class)
        fun hideKeyboard(v: android.view.View?)

        @StateStrategyType(SkipStrategy::class)
        fun showKeyboard()

        @StateStrategyType(SkipStrategy::class)
        fun showNoConnectionMessage(show: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showRequestErrorMessage()

        @StateStrategyType(SkipStrategy::class)
        fun showEmailErrorMessage()
    }

    interface LoadingView {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLoadingDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideLoadingDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideAllLoadingDialogs()
    }

    interface Presenter
}