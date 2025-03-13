package com.example.ui.base

import androidx.annotation.StringRes
import com.example.data.models.UserDetail
import com.example.ui.views.dialogs.StateType
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.Skip

interface BaseContract {
    interface View : MvpView, LoadingView {
        @StateStrategyType(SkipStrategy::class)
        fun showSnackBar(@StringRes message: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showSnackBar(message: String)

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
        fun showAddedToFavoriteDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showRemovedFromFavoriteDialog()

        @StateStrategyType(SkipStrategy::class)
        fun setIgnoreTokenListener(isIgnore: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRegistrationSuccessDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showBrowser(url : String)
    }

    interface LoadingView {
        @StateStrategyType(SkipStrategy::class)
        fun showProgressBarLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideProgressBarLoading()

        @StateStrategyType(SkipStrategy::class)
        fun showProgressBarDialogLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideProgressBarDialogLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideAllLoadingDialogs()

        @StateStrategyType(SkipStrategy::class)
        fun showCustomLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideCustomLoading()
    }

    interface LoadingEventView {
        @StateStrategyType(SkipStrategy::class)
        fun showEventLoading(pos : Int)

        @StateStrategyType(SkipStrategy::class)
        fun hideEventLoading(pos : Int)
    }

    interface Presenter {

    }
}