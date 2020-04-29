package com.example.ui.user.phoneconfirm

import com.example.ui.base.BaseContract

interface PhoneConfirmContract {
    interface View : BaseContract.View {
        fun setCanResend(canResend: Boolean)
        fun setTimeLeft(time: String?)
        fun setPhone(phone: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onResendClick()
    }
}
