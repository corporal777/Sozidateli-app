package com.example.ui.state.maxNew.interests

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.InterestNew
import com.example.data.models.UserInterest
import com.example.ui.state.maxNew.base.BaseMaxStateContract

interface MaxStatusInterestsContract {
    interface View : BaseMaxStateContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>)
    }

    interface Presenter : BaseMaxStateContract.Presenter {
        fun onSaveInterestsClick(data: List<InterestNew>)
    }
}