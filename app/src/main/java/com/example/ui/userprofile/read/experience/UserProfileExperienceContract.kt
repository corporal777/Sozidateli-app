package com.example.ui.userprofile.read.experience

import com.example.ui.userprofile.base.BaseUserProfileContract
import moxy.viewstate.strategy.alias.OneExecution

interface UserProfileExperienceContract {
    interface View : BaseUserProfileContract.View {

        @OneExecution
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
