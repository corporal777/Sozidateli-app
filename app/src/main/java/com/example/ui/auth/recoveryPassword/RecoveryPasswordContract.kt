package com.example.ui.auth.recoveryPassword

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface RecoveryPasswordContract {
    interface View : BaseContract.View {
        @OneExecution
        fun enableRecoveryBtn(isEnable: Boolean)

        @OneExecution
        fun setEmail(email: String)

        @OneExecution
        fun showEmailError(show: Boolean)

        @OneExecution
        fun showRecoveryNotification(email: String, userId : String)

        @OneExecution
        fun showWrongEmailError()

        @OneExecution
        fun setTimeLeft(seconds: Int)

        @OneExecution
        fun showPasswordSuccessUpdated()
    }

    interface Presenter : BaseContract.Presenter {
        fun onRecoveryClick()
        fun onChangeEmailText(email: String)
        fun onCloseClick()
        fun onSetPassword(code: String, password: String, userId: String)
        fun sendCodeAgain()
    }
}
