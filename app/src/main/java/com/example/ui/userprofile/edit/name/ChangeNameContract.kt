package com.example.ui.userprofile.edit.name

import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution

interface ChangeNameContract {
    interface View : BaseBottomSheetContract.View {
        @OneExecution
        fun setUserName(
            name: String?,
            lastName: String?,
            middleName: String?,
            isMiddleNameAbsent: Boolean
        )
        @OneExecution
        fun enableMiddleNameInput(enable : Boolean)

        @OneExecution
        fun enableBtnSave(isEnable :Boolean)

        @OneExecution
        fun showFirstNameError(show: Boolean)

        @OneExecution
        fun showLastNameError(show: Boolean)

        @OneExecution
        fun showMiddleNameError(show: Boolean)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onSaveNameClick()
        fun performChangeName(value: String?)
        fun performChangeLastName(value: String?)
        fun performChangeMiddleName(value: String?)
        fun performSetNoMiddleName(isHas : Boolean)
    }
}