package com.example.ui.userprofile.read.maindata

import com.example.ui.userprofile.base.BaseUserProfileContract
import moxy.viewstate.strategy.alias.OneExecution

interface UserProfileMainDataContract {
    interface View : BaseUserProfileContract.View {

        @OneExecution
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
