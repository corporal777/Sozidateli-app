package com.example.ui.userprofile.read.settings.confirm_phone_email

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface ConfirmEmailPhoneContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun setButtonSendAgain(enable: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun setTimeLeft(time: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContentType(type : String)

        @StateStrategyType(SkipStrategy::class)
        fun showProgressLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideProgressLoading()

        @StateStrategyType(SkipStrategy::class)
        fun setEmailPhoneIsConfirmed()

        @StateStrategyType(SkipStrategy::class)
        fun setCodeError(show : Boolean)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun startTimerForResendCode()
        fun sendCode()
        fun confirmEmailPhone(email : String, code : String)
    }
}