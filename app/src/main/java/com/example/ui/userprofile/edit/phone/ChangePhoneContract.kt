package com.example.ui.userprofile.edit.phone

import com.example.data.models.FieldDetails
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangePhoneContract {
    interface View : BaseBottomSheetContract.View {

        @OneExecution
        fun setUserPhone(phone : String?)

        @OneExecution
        fun setUserPhoneIsConfirmed(isConfirmed : Boolean)

        @OneExecution
        fun setUserPhoneIsVisible(isVisible : Boolean)

        @OneExecution
        fun showPhoneIsUpdatedSuccessfully(phone : FieldDetails?)

        @Skip
        fun showEnterPassword(phone: String)

        @Skip
        fun hideEnterPassword()

        @Skip
        fun showPhoneNotUnique(phone: String)

        @Skip
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