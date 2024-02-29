package com.example.ui.userprofile.common.email

import com.example.ui.base.BaseContract
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangeEmailContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setCurrentEmail(currentEmail : String?)

        @Skip
        fun showEmailError(show: Boolean)

        @Skip
        fun showEmailNotUnique(email: String)

        @Skip
        fun showEmailConfirm(email: String)

        @Skip
        fun enableBtnSave(enable: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onCheckEmailIsUnique()
        fun onShowEmailConfirm()
        fun onChangeEmailText(email: String)
    }
}