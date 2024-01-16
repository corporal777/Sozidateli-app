package com.example.ui.userprofile.edit.password.reset

import com.example.ui.base.BaseContract
import com.example.ui.base.BaseFragment
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
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