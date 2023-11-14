package com.example.ui.state.maxNew.base

import com.example.ui.base.BaseContract
import com.example.ui.state.maxNew.MaxStateScreenType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface BaseMaxStateContract {
    interface View : BaseContract.View {
        @OneExecution
        fun goToNextScreen(screenType: MaxStateScreenType)

        @Skip
        fun showMaxStateDone(screen: Int)

        @Skip
        fun setClickClose(type : Int)

        @Skip
        fun showUpdateError(message: String? = null)

        @Skip
        fun buttonNextEnabled(enabled: Boolean)

        @OneExecution
        fun showAddEmailDialog()

        @OneExecution
        fun hideAddEmailDialog()

        @OneExecution
        fun showEmailIsNotUnique(email: String)

        @OneExecution
        fun showEmailConfirmation(email: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowMaxStateDone()
        fun onClickClose()

        fun checkEmailIsUnique(email: String)
        fun onShowEmailConfirm(email : String)

        fun checkUserEmail()
    }
}