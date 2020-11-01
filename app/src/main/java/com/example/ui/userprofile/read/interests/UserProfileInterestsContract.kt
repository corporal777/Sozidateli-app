package com.example.ui.userprofile.read.interests

import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Interest
import com.example.data.models.user.User
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileInterestsContract {
    interface View : BaseUserProfileContract.View {

        override fun onUserUpdated(user: User?) = Unit

        @StateStrategyType(AddToEndStrategy::class)
        fun onInterestsUpdated(interests: Map<Interest, List<Interest>>)

        @StateStrategyType(SkipStrategy::class)
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
