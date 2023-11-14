package com.example.ui.userprofile.edit.password

import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangePasswordContract {
    interface View : BaseBottomSheetContract.View {

        @OneExecution
        fun showEnterNewPassword()

        @OneExecution
        fun setPasswordIsNotCorrect(attempts: Int)

        @OneExecution
        fun showPasswordSuccessUpdated()

        @Skip
        fun showLoginAgainDialog()

        @OneExecution
        fun showRecoveryPassword()
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onChangePasswordClickConfirm(newPassword: String)
        fun checkPasswordValid(password: String)
        fun logoutFromAccount()
        fun onRecoveryPasswordClick()
    }
}