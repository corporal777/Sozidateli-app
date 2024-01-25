package com.example.ui.auth.confirm.email

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ConfirmEmailCodeContract  {
    interface View : BaseContract.View {
        @OneExecution
        fun setEmail(email: String?)

        @Skip
        fun setTimeLeft(seconds: Int)

        @Skip
        fun setCanSendAgain(canSend: Boolean)

        @Skip
        fun setConfirmButton(enabled: Boolean)

        @Skip
        fun showCodeError(show: Boolean)

        @Skip
        fun showCustomLoading(type: Int)

        @Skip
        fun hideCustomLoading(type: Int)

        @OneExecution
        fun setFinishRegister(isFinish : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onConfirmEmail()
        fun onSendCodeAgain()
        fun onChangeCode(code : String)
    }
}