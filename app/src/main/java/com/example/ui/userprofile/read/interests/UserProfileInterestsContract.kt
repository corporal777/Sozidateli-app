package com.example.ui.userprofile.read.interests

import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.ui.userprofile.base.BaseUserProfileContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserProfileInterestsContract {
    interface View : BaseUserProfileContract.View {

        @Skip
        override fun setUserData(user: UserDetail, state: String) = Unit

        @AddToEndSingle
        fun showInterestsPlaceholder()

        @AddToEndSingle
        fun onInterestsUpdated(interests: Map<InterestNew, List<InterestNew>>)

        @OneExecution
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
