package com.example.ui.userprofile.edit.phone

import com.example.data.models.FieldDetails
import com.example.ui.base.BaseContract
import com.example.ui.base.BaseFragment
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
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
        fun enableBtnConfirm(isConfirmed: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangePhone(phone: String)
        fun onChangePhoneVisible(isVisible: Boolean)

        fun onShowConfirmClick(withAdd : Boolean)
        fun onSavePhoneClick(withCheck : Boolean)
    }
}