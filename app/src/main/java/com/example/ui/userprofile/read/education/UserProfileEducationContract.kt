package com.example.ui.userprofile.read.education

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileEducationContract {
    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter, BaseContract.OnChangeElevation {
        fun onEditClick()
    }
}
