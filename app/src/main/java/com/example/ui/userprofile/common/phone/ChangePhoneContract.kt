package com.example.ui.userprofile.common.phone

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangePhoneContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setUserPhone(phone : String?, isVisible: Boolean)

        @Skip
        fun showPhoneNotUnique(phone: String)

        @Skip
        fun showPhoneConfirmation(phone: String, withAdd : Boolean)

        @Skip
        fun enableBtnSave(enabled: Boolean)

        @Skip
        fun enableBtnConfirm(isConfirmed: Boolean, isValid : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangePhone(phone: String)
        fun onChangePhoneVisible(isVisible: Boolean)

        fun onShowConfirmClick(withAdd : Boolean)
        fun onSavePhoneClick(withCheck : Boolean)
    }
}