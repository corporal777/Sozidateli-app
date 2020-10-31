package com.example.ui.userprofile.maindataedit

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.userprofile.base.BaseUserProfileContract

interface UserProfileMainDataEditContract {
    interface View : BaseUserProfileContract.View {

        @StateStrategyType(SkipStrategy::class)
        fun showEmptyLastNameError()

        @StateStrategyType(SkipStrategy::class)
        fun showEmptyNameError()

        @StateStrategyType(SkipStrategy::class)
        fun showEmptyMiddleNameError()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onSaveClick(
                lastName: String?,
                name: String?,
                middleName: String?,
                noMiddleName: Boolean
        )
    }
}
