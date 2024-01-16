package com.example.ui.auth.login

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface LoginContract {
    interface View : BaseContract.View {
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
        fun showAccountBlockingDialog()
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangeLoginText(value: String)
        fun onChangePasswordText(value: String)
        fun onClickLogin(invite: Int)
        fun onClickRecoverPassword()
    }
}
