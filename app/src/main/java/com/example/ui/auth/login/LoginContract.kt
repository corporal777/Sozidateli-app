package com.example.ui.auth.login

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface LoginContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmailAndPassword(email: String, password: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegister()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRecoveryPassword(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableLoginBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegistration(snUser: SnUser? = null)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegistrationConfirmation(show: Boolean)
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickBack()
        fun onChangeEmailText(email: String)
        fun onChangePasswordText(password: String)
        fun onClickLogin(email: String, password: String)
        fun onClickRecoverPassword()

        fun onRegistrationCancel()
        fun onRegistrationConfirm()
    }
}
