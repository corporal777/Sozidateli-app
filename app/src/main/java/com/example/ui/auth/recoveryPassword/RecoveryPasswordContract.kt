package com.example.ui.auth.recoveryPassword

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface RecoveryPasswordContract {
    interface View : BaseContract.View {
        @Skip
        fun enableRecoveryBtn(isEnable: Boolean)

        @OneExecution
        fun setEmail(email: String)

        @Skip
        fun showEmailError(show: Boolean)

        @OneExecution
        fun showEmailRecovery(email: String, userId : String)

        @OneExecution
        fun showPhoneRecovery(phone: String, userId : String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onRecoveryClick()
        fun onChangeEmailText(email: String)
    }
}
