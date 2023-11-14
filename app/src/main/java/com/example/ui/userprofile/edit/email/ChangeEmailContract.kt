package com.example.ui.userprofile.edit.email

import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangeEmailContract {
    interface View : BaseBottomSheetContract.View {

        @OneExecution
        fun setCurrentEmail(currentEmail : String)

        @Skip
        fun showEmailNotValid(email: String)

        @Skip
        fun showEmailNotUnique(email: String)

        @Skip
        fun showChangeEmailComplete()

        @Skip
        fun showEmailConfirm(email: String)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onShowEmailConfirm(email : String)
        fun checkEmailIsUnique(email: String)
        fun updateEmail(email: String)

    }
}