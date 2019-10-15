package com.example.ui.base

import android.content.DialogInterface
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
        fun navigateUp()

        @StateStrategyType(SkipStrategy::class)
        fun hideKeyboard()

        @StateStrategyType(SkipStrategy::class)
        fun hideKeyboard(v: android.view.View?)

        @StateStrategyType(SkipStrategy::class)
        fun showKeyboard()

        @StateStrategyType(SkipStrategy::class)
        fun showToast(message: String)

        @StateStrategyType(SkipStrategy::class)
        fun showToast(messagesIds: List<Int>)
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