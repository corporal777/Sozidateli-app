package com.example.ui.auth.register

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserRegistrationContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(
            lastName: String?,
            firstName: String?,
            middleName: String?,
            isMiddleNameAbsent : Boolean,
            phone: String?,
            birthday: String?,
            password: String?,
            isAgree: Boolean
        )

        @Skip
        fun enableRegisterBtn(isEnable: Boolean)

        @Skip
        fun showLastNameError(show: Boolean, error : String?)

        @Skip
        fun showFirstNameError(show: Boolean, error : String?)

        @Skip
        fun showMiddleNameError(show: Boolean, error : String?)

        @Skip
        fun showLoginError(show: Boolean)

        @Skip
        fun showBirthdayError(show: Boolean)

        @Skip
        fun enableMiddleNameInput(enable: Boolean)

        @Skip
        fun showPasswordError(show: Boolean)

        @Skip
        fun showUserAgreementError(show: Boolean)

        @Skip
        fun showPhoneIsNotUnique(login: String)

        @OneExecution
        fun showCodeConfirmation(login: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangeLastNameText(lastName : String)
        fun onChangeFirstNameText(firstName : String)
        fun onChangeMiddleNameText(middleName: String)
        fun onMiddleNameIsAbsent(checked: Boolean)
        fun onChangeLoginText(login: String)
        fun onChangeBirthdayText(birthday: String)
        fun onChangePasswordText(password: String?, isValid: Boolean)
        fun onChangeUserAgreement(isAgree : Boolean)
        fun registerUser(withCheck : Boolean)

    }
}