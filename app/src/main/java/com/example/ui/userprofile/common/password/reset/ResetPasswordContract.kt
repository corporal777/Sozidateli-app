package com.example.ui.userprofile.common.password.reset

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ResetPasswordContract {
    interface View : BaseContract.View {
        @Skip
        fun showPasswordSuccessChanged()

        @Skip
        fun enableBtnReset(enable : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSavePasswordClick()
        fun onRecoveryPasswordClick()
        fun onChangePasswordText(password: String?, isValid: Boolean)
    }
}