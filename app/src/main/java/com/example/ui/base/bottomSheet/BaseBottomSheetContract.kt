package com.example.ui.base.bottomSheet

import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.ui.views.StateType

interface BaseBottomSheetContract {
    interface View : MvpView, BaseContract.LoadingView {

        @StateStrategyType(SkipStrategy::class)
        fun showRequestErrorMessage()

        @StateStrategyType(SkipStrategy::class)
        fun showKeyboard(v: android.view.View?)

        @StateStrategyType(SkipStrategy::class)
        fun hideKeyboard(v: android.view.View?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideBottomSheetDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showToast(message : String)

        @StateStrategyType(SkipStrategy::class)
        fun navigateUp()
    }



    interface Presenter : BaseContract.Presenter {
    }
}