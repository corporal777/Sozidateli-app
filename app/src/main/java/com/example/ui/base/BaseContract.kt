package com.example.ui.base

import android.content.DialogInterface
import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
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
        fun showDialog(message: String?)

        @StateStrategyType(SkipStrategy::class)
        fun showDialog(message: String?,onOkClickListener: DialogInterface.OnClickListener?)

        @StateStrategyType(SkipStrategy::class)
        fun showDialog(title: String?,message: String?,onOkClickListener: DialogInterface.OnClickListener?)

        @StateStrategyType(SkipStrategy::class)
        fun showDialog(title: String?,message: String?)

        @StateStrategyType(SkipStrategy::class)
        fun showToast(messagesIds: List<Int>)

        @StateStrategyType(SkipStrategy::class)
        fun showErrorDialog(messageIds:List<Int>,onDismissListener: DialogInterface.OnDismissListener?)
    }

    interface LoadingView {
        @StateStrategyType(SkipStrategy::class)
        fun showLoadingDialog()

        @StateStrategyType(SkipStrategy::class)
        fun hideLoadingDialog()

        @StateStrategyType(SkipStrategy::class)
        fun hideAllLoadingDialogs()
    }

    interface Presenter{
        @StateStrategyType(SkipStrategy::class)
        fun onError(errors:List<String>)
    }
}