package com.example.ui.auth.register

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserRegistrationContract {
    interface View : BaseAuthContract.View {
        @Skip
        fun enableRegisterBtn(isEnable: Boolean)

        @OneExecution
        fun showLastNameError(show: Boolean, error : String?)

        @OneExecution
        fun showFirstNameError(show: Boolean, error : String?)

        @OneExecution
        fun showMiddleNameError(show: Boolean, error : String?)

        @OneExecution
        fun showMobilePhoneError(show: Boolean)

        @OneExecution
        fun enableMiddleNameInput(enable: Boolean)

        @OneExecution
        fun showUserAgreementError(show: Boolean)
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onChangeLastNameText(lastName : String)
        fun onChangeFirstNameText(firstName : String)
        fun onChangeMiddleNameText(middleName: String)
        fun onNoMiddleNameChecked(checked: Boolean)
        fun onChangeMobilePhoneText(phone: String)
        fun onChangePasswordText(password: String?, isValid: Boolean)
        fun onChangeUserAgreement(isAgree : Boolean)
        fun registerUser()
    }
}