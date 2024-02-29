package com.example.ui.userprofile.common.password

import com.example.ui.base.BaseContract
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangePasswordContract {
    interface View : BaseContract.View {

        @OneExecution
        fun showEnterCurrentPassword()

        @OneExecution
        fun showEnterNewPassword()

        @Skip
        fun setPasswordIsNotCorrect(attempts: Int)

        @Skip
        fun showLoginAgainDialog()

        @OneExecution
        fun showRecoveryPassword()
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangePasswordClick(newPassword: String)
        fun onCheckPasswordValid(password: String)
        fun logoutFromAccount()
    }
}