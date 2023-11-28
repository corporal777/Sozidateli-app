package com.example.ui.auth.register

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserRegistrationContract {
    interface View : BaseAuthContract.View {
        @Skip
        fun enableRegisterBtn(isEnable: Boolean)

        @Skip
        fun showLastNameError(show: Boolean, error : String?)

        @Skip
        fun showFirstNameError(show: Boolean, error : String?)

        @Skip
        fun showMiddleNameError(show: Boolean, error : String?)

        @Skip
        fun showMobilePhoneError(show: Boolean)

        @Skip
        fun enableMiddleNameInput(enable: Boolean)

        @Skip
        fun showPasswordError(show: Boolean)

        @Skip
        fun showUserAgreementError(show: Boolean)

        @Skip
        fun showPhoneIsNotUnique(phone: String)

        @OneExecution
        fun showPhoneCodeConfirmation(phone: String)

        @Skip
        fun changeAppBarHeader(value: Float)
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onChangeLastNameText(lastName : String)
        fun onChangeFirstNameText(firstName : String)
        fun onChangeMiddleNameText(middleName: String)
        fun onMiddleNameIsAbsent(checked: Boolean)
        fun onChangeMobilePhoneText(phone: String)
        fun onChangePasswordText(password: String?, isValid: Boolean)
        fun onChangeUserAgreement(isAgree : Boolean)
        fun registerUser(withCheck : Boolean)

        fun onScrollChange(value : Int)
    }
}