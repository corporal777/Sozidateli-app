package com.example.ui.auth.register.email.newbuild

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface RegisterEmailContract {
    interface View : BaseAuthContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSnRegistration(snUser: SnUser)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFinishRegister(
            name: String,
            lastName: String,
            middleName: String?,
            email: String,
            code: String,
            isNoMiddleName: Boolean
        )

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFirstNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLastNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAgreementError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongPhoneError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableMiddleNameInput(enable: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showAgreementSelection(isValid: Boolean)
    }

    interface Presenter : BaseAuthContract.Presenter {
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
