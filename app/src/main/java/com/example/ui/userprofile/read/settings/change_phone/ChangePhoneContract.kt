package com.example.ui.userprofile.read.settings.change_phone

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.FieldDetails
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface ChangePhoneContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserPhone(phone : String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserPhoneIsConfirmed(isConfirmed : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserPhoneIsVisible(isVisible : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showConfirmPhoneDialog(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun setTimerForResendConfirmCode(seconds : Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPhoneIsUpdatedSuccessfully()

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String, type: ChangePhonePresenter.ConfirmType)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun setNewPhone(phone: String)
        fun setNewPhoneIsConfirmed()
        fun setNewPhoneIsVisible(isVisible: Boolean)
        fun updatePhoneData()
        fun onSendCodeClick()
        fun onConfirmPhoneClick(phone : String)
        fun onConfirmCodeClick(code : String)
        fun startTimerForResendCode(phone: String)

        fun checkPhoneIsUnique(phone : String)
    }
}