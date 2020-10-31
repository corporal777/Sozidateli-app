package com.example.ui.userprofile.maindata

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileMainDataContract {
    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
