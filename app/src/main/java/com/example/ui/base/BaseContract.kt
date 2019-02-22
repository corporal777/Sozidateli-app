package com.example.ui.base

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BaseContract {
    interface View : MvpView,LoadingView {
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
    }

    interface LoadingView {

        @StateStrategyType(SkipStrategy::class)
        fun showLoadingDialog()

        @StateStrategyType(SkipStrategy::class)
        fun hideLoadingDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showToast(message: String)
    }

    interface Presenter
}