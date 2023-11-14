package com.example.ui.auth.login

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import moxy.viewstate.strategy.alias.OneExecution

interface LoginContract {
    interface View : BaseAuthContract.View {
        @OneExecution
        fun setLoginAndPassword(login: String, password: String)

        @OneExecution
        fun showRecoveryPassword(email: String)

        @OneExecution
        fun enableLoginBtn(isEnable: Boolean)

        @OneExecution
        fun showLoginError(show: Boolean)

        @OneExecution
        fun showPasswordError(show: Boolean)

        @OneExecution
        fun showWrongPasswordError()

        @OneExecution
        fun showSnRegistration(snUser: SnUser)


    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickBack()
        fun onChangeLoginText(value: String)
        fun onChangePasswordText(value: String)
        fun onClickLogin(invite: Int)
        fun onClickRecoverPassword()
    }
}
