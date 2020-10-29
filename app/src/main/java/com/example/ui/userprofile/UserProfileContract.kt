package com.example.ui.userprofile

import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface UserProfileContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndStrategy::class)
        fun setUser(user: User?)
    }

    interface Presenter : BaseContract.Presenter {

        fun onEditAvatarClick()
        fun onMainDataClick()
        fun onContactsClick()
        fun onInterestsClick()
        fun onEducationClick()
        fun onExperienceClick()
    }
}
