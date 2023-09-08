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
        fun showPhoneIsUpdatedSuccessfully(phone : FieldDetails?)

        @StateStrategyType(SkipStrategy::class)
        fun showEnterPassword(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun hideEnterPassword()

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirmation(phone: String)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun setNewPhone(phone: String)
        fun setNewPhoneIsConfirmed(phone: String)
        fun setNewPhoneIsVisible(isVisible: Boolean)

        fun updatePhoneData()
        fun onShowPhoneConfirm(phone : String)
        fun onSaveNewPhoneClick(phone : String)

        fun checkPassword(password : String, phone: String)
        fun checkPhoneIsUnique(phone : String)
    }
}