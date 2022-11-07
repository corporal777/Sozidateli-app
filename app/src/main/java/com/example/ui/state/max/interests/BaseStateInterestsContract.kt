package com.example.ui.state.max.interests

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.InterestNew
import com.example.data.models.UserInterest
import com.example.ui.base.BaseContract

interface BaseStateInterestsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>)

        @StateStrategyType(SkipStrategy::class)
        fun goToNext()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setClickClose(type : Int)
    }
    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun onClickClose()
        fun onSaveInterestsClick(data: List<InterestNew>)
    }
}