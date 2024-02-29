package com.example.ui.userprofile.common.password.confirm

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface PhoneConfirmPasswordContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setMobilePhone(mobilePhone: String?)

        @Skip
        fun setTimeLeft(seconds: Int)

        @Skip
        fun setCanCallAgain(canCall: Boolean)

        @Skip
        fun setConfirmButton(enabled: Boolean)

        @Skip
        fun showCodeError(show: Boolean)

        @Skip
        fun showCustomLoading(type: Int)

        @Skip
        fun hideCustomLoading(type: Int)

        @OneExecution
        fun showResetPasswordFragment(code: String, userId : String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onConfirmMobilePhone()
        fun onSendCallAgain()
        fun onChangeCode(code : String)
    }
}