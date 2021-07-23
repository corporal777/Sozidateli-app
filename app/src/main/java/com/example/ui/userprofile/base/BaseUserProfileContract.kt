package com.example.ui.userprofile.base

import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface BaseUserProfileContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndStrategy::class)
        fun onUserUpdated(user: UserDetail?, state: String)
    }

    interface Presenter : BaseContract.Presenter
}
