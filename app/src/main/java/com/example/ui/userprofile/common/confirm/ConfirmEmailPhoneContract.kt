package com.example.ui.userprofile.common.confirm

import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ConfirmEmailPhoneContract {
    interface View : BaseBottomSheetContract.View {

        @Skip
        fun setButtonSendAgain(enable: Boolean)

        @Skip
        fun setTimeLeft(time: Int)

        @OneExecution
        fun setContentType(type : String)

        @Skip
        fun setEmailPhoneIsConfirmed()

        @Skip
        fun setCodeError(show : Boolean)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun sendCodeAgain()
        fun confirmEmailPhone(email : String, code : String)
    }
}