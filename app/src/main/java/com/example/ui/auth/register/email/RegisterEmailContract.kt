package com.example.ui.auth.register.email

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface RegisterEmailContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(email: String?, firstName: String?, lastName: String?, password: String?, passwordConfirm: String?, isAgree: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSnRegistration(snUser: SnUser)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailConfirmation(email: String, password: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFirstNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLastNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordConfirmError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAgreementError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserAgreement()
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickClose()
        fun onClickUserAgreement()
        fun onClickRegister(email: String?, firstName: String?, lastName: String?, password: String?, passwordConfirm: String?, isAgree: Boolean)
        fun onClickAgree(isAgree: Boolean)

        fun onChangeEmailText(email: String)
        fun onChangeFirstNameText(firstName: String)
        fun onChangeLastNameText(lastName: String)
        fun onChangePasswordText(password: String)
        fun onChangePasswordConfirmText(password: String)
    }
}
