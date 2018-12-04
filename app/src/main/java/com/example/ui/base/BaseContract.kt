package com.example.ui.base

import android.support.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BaseContract {
    interface View : MvpView {
        @StateStrategyType(SkipStrategy::class)
        fun showToast(@StringRes message: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showToast(message: String)

        @StateStrategyType(SkipStrategy::class)
        fun onBack()
    }

    interface Presenter
}