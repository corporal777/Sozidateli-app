package com.example.ui.userprofile.edit.shortName

import com.example.data.models.UserDetail
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangeShortNameContract {
    interface View : BaseBottomSheetContract.View{

        @OneExecution
        fun setUserShortName(name : String)

        @Skip
        fun setUserShortNameUnique(isUnique : Boolean)

        @OneExecution
        fun showUserShortNameSuccessUpdated()

        @OneExecution
        fun updateUserShortNameInProfile(user : UserDetail)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun checkUserShortNameUnique(short: String)
        fun updateUserShortName(short: String)
    }
}