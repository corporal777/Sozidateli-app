package com.example.ui.auth.register.sn

import com.example.data.models.AuthResponse
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SnRegisterContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setUserData(
            lastName: String?,
            firstName: String?,
            middleName: String?,
            middleNameIsAbsent: Boolean,
            mobilePhone: String?,
            email: String?,
            birthday: String?
        )

        @Skip
        fun setMiddleNameAbsent(isAbsent: Boolean)

        @Skip
        fun enableContinueButton(isEnable: Boolean)

        @Skip
        fun showLastNameError(show: Boolean)

        @Skip
        fun showFirstNameError(show: Boolean)

        @Skip
        fun showMiddleNameError(show: Boolean)

        @Skip
        fun showPhoneError(show: Boolean)

        @Skip
        fun showEmailError(show: Boolean)

        @Skip
        fun showBirthdayError(show: Boolean)

        @OneExecution
        fun showEmailConfirmation(email: String?, auth : AuthResponse)

        @Skip
        fun showEmailIsNotUnique(email: String?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickContinue(withCheck : Boolean)
        fun onChangeLastName(lastName: String)
        fun onChangeFirstName(firstName: String)
        fun onChangeMiddleName(middleName: String)
        fun onMiddleNameIsAbsent(isAbsent: Boolean)
        fun onChangeMobilePhone(phone: String)
        fun onChangeBirthday(birthday: String)
        fun onChangeEmail(email: String)
    }
}
