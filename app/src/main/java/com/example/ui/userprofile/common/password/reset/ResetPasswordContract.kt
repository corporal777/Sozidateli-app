package com.example.ui.userprofile.common.password.reset

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.Skip

interface ResetPasswordContract {
    interface View : BaseContract.View {
        @Skip
        fun showPasswordError(show : Boolean)

        @Skip
        fun enableBtnResetPassword(enable : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onRecoveryPasswordClick()
        fun onChangePasswordText(password: String?, isValid: Boolean)
    }
}