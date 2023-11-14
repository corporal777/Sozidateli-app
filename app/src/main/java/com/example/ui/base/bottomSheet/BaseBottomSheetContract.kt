package com.example.ui.base.bottomSheet

import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface BaseBottomSheetContract {
    interface View : MvpView, BaseContract.LoadingView {

        @Skip
        fun showRequestErrorMessage()

        @Skip
        fun showKeyboard(v: android.view.View?)

        @Skip
        fun hideKeyboard(v: android.view.View?)

        @OneExecution
        fun hideBottomSheetDialog()

        @Skip
        fun showToast(message : String)

        @Skip
        fun navigateUp()
    }



    interface Presenter : BaseContract.Presenter {
    }
}