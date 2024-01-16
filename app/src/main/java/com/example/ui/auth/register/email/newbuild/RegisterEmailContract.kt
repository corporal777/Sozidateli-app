package com.example.ui.auth.register.email.newbuild

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface RegisterEmailContract {
    interface View : BaseContract.View {
        @OneExecution
        fun enableRegisterBtn(isEnable: Boolean)

        @OneExecution
        fun showFinishRegister(
            name: String,
            lastName: String,
            middleName: String?,
            email: String,
            code: String,
            isNoMiddleName: Boolean
        )

        @OneExecution
        fun showFirstNameError(show: Boolean)

        @OneExecution
        fun showLastNameError(show: Boolean)

        @OneExecution
        fun showEmailError(show: Boolean)

        @OneExecution
        fun showAgreementError(show: Boolean)

        @OneExecution
        fun showWrongPhoneError(show: Boolean)

        @OneExecution
        fun enableMiddleNameInput(enable: Boolean)

        @Skip
        fun showEmailNotUnique(email: String)

        @Skip
        fun showPhoneNotUnique(email: String)

        @Skip
        fun showAgreementSelection(isValid: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickClose()
        fun onClickRegister()

        fun onChangeEmailText(email: String)

        fun onChangeFirstNameText(firstName: String)
        fun onChangeLastNameText(lastName: String)
        fun onChangeMiddleNameText(middleName: String)
        fun onNoMiddleNameChecked(checked: Boolean)

        fun onChangePasswordText(password: String, isValid: Boolean)

        fun onClickAgree(isAgree: Boolean)
        fun register(withCheck : Boolean)
    }
}
