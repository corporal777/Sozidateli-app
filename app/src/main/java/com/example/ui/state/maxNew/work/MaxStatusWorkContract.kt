package com.example.ui.state.maxNew.work

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.data.models.WorkExperienceServerModel
import com.example.ui.state.maxNew.base.BaseMaxStateContract

interface MaxStatusWorkContract {
    interface View : BaseMaxStateContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setWorkData(user: UserDetail)
    }
    interface Presenter : BaseMaxStateContract.Presenter {
        fun onSaveWorkClick(data: WorkExperienceServerModel)
    }
}