package com.example.ui.userprofile.read.settings.change_phone.confirm_phone

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.ui.userprofile.read.settings.change_phone.ChangePhonePresenter

interface ConfirmPhoneContract {
    interface View : BaseBottomSheetContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun setResendButtonEnable(seconds: Int)

        @StateStrategyType(SkipStrategy::class)
        fun setCanStartTimer()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContentVisible(canShow : Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showProgressLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideProgressLoading()
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun startTimerForResendCode()
        fun sendCode()
    }
}