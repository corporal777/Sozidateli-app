package com.example.ui.auth.login

import android.content.Context
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface LoginContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setLoginAndPassword(login: String, password: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRecoveryPassword(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableLoginBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLoginError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongPasswordError()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailRegistration()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSnRegistration(snUser: SnUser)

    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickBack()
        fun onChangeLoginText(login: String, context: Context)
        fun onChangePasswordText(password: String, context: Context)
        fun onClickLogin(login: String, password: String)
        fun onClickRecoverPassword()
    }
}
