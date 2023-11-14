package com.example.ui.auth.confirm

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface EmailConfirmContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setTimeLeft(seconds: Int)

        @OneExecution
        fun setCanResend(canResend: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onResendClick()
        fun onCloseClick()
    }
}
