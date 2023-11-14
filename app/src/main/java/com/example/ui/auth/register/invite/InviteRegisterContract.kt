package com.example.ui.auth.register.invite

import com.example.ui.auth.base.BaseAuthContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface InviteRegisterContract {
    interface View : BaseAuthContract.View {
        @AddToEndSingle
        fun setData(firstName: String?, lastName: String?, middleName: String?, email: String?)

        @OneExecution
        fun showUserAgreement()

        @OneExecution
        fun enableRegisterBtn(isEnable: Boolean)

        @OneExecution
        fun showFirstNameError(show: Boolean)

        @OneExecution
        fun showLastNameError(show: Boolean)

        @OneExecution
        fun showEmailError(show: Boolean)

        @AddToEndSingle
        fun enableMiddleNameInput(enable: Boolean)

        @AddToEndSingle
        fun showEmailDialog(email: String)

        @AddToEndSingle
        fun openHome()

        @AddToEndSingle
        fun loggedOut()
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickRegister()

        fun onClickClose()
        fun onClickUserAgreement()

        fun onNoMiddleNameChecked(checked: Boolean)
        fun onAgreeChecked(checked: Boolean)

        fun onChangeFirstNameText(value: String)
        fun onChangeLastNameText(value: String)
        fun onChangeMiddleNameText(value: String)
        fun onChangePasswordText(value: String?, isValid: Boolean)
    }
}