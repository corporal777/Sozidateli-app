package com.example.ui.base.bottomSheet

import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface BaseBSContract {
    interface View : MvpView, BaseContract.LoadingView {

        @OneExecution
        fun hideBottomSheetFragment()

        @Skip
        fun showRequestErrorMessage()

        @Skip
        fun setIgnoreTokenListener(isIgnore: Boolean)
    }



    interface Presenter : BaseContract.Presenter {
    }
}