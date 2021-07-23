package com.example.ui.userprofile.read.interests

import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Interest
import com.example.data.models.UserDetail
import com.example.data.models.InterestNew
import com.example.data.models.InterestsModel
import com.example.data.models.user.User
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileInterestsContract {
    interface View : BaseUserProfileContract.View {

        override fun onUserUpdated(user: UserDetail?, state: String) = Unit

        @StateStrategyType(AddToEndStrategy::class)
        fun onInterestsUpdated(interests: Map<InterestNew, List<InterestNew>>)

        @StateStrategyType(SkipStrategy::class)
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
