package com.example.ui.profile.shortName

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface ChangeShortNameContract {
    interface View : BaseBottomSheetContract.View{

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserShortName(name : String)

        @StateStrategyType(SkipStrategy::class)
        fun setUserShortNameUnique(isUnique : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserShortNameSuccessUpdated()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateUserShortNameInProfile(user : UserDetail)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun checkUserShortNameUnique(short: String)
        fun updateUserShortName(short: String)
    }
}