package com.example.ui.userprofile.common.shortName

import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangeShortNameContract {
    interface View : BaseContract.View{

        @OneExecution
        fun setUserShortName(name : String?, id : String)

        @Skip
        fun setUserShortNameUnique(isUnique : Boolean, name : String?)

        @Skip
        fun enableBtnSave(enabled : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangeShortName(short: String)
        fun onSaveShortName()
    }
}