package com.example.ui.base

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.views.StateType

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
        fun showNotificationErrorMessage()

        @StateStrategyType(SkipStrategy::class)
        fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?)

        @StateStrategyType(SkipStrategy::class)
        fun showErrorMessage(canGoBack : Boolean, message: String)

        @StateStrategyType(SkipStrategy::class)
        fun setAppBarElevation(value : Float)

        @StateStrategyType(SkipStrategy::class)
        fun setToolbarTitle(title : String)
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
        fun enableBackClick()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun disableBackClick()
    }

    interface Presenter {
    }

    interface OnChangeElevation {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeAppBarElevation(value : Int)
    }
}