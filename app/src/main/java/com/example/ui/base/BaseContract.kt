package com.example.ui.base

import androidx.annotation.StringRes
import com.example.data.models.UserDetail
import com.example.ui.views.dialogs.StateType
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

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

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneErrorMessage()

        @StateStrategyType(SkipStrategy::class)
        fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?)

        @StateStrategyType(SkipStrategy::class)
        fun showErrorMessage(canGoBack: Boolean, message: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventAddedToFavoriteDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showEventRemovedFromFavoriteDialog()

        @StateStrategyType(SkipStrategy::class)
        fun setIgnoreTokenListener(isIgnore: Boolean)
    }

    interface LoadingView {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLoadingDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideLoadingDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showCustomProgressDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideCustomProgressDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showProgressBarLoadingDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideProgressBarLoadingDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideAllLoadingDialogs()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showCustomLoading()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideCustomLoading()
    }

    interface Presenter {

    }

    interface OnChangeElevation {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeAppBarElevation(value: Int)
    }
}