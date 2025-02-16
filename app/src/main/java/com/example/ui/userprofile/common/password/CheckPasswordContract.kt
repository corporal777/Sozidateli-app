package com.example.ui.userprofile.common.password

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface CheckPasswordContract {
    interface View : BaseContract.View {

        @Skip
        fun setPasswordIsNotCorrect(attempts: Int)

        @Skip
        fun showLoginAgainDialog()

        @OneExecution
        fun showRecoveryPassword()

        @OneExecution
        fun showChangePassword()

        @Skip
        fun enableBtnChange(enable : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onCheckPasswordValid()
        fun logoutFromAccount()
    }
}