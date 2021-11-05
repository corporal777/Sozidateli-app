package com.example.ui.editwork


import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.data.models.WorkExperienceServerModel
import com.example.ui.base.BaseContract

interface EditWorksContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setWorkData(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSaveWorkClick(data: WorkExperienceServerModel)
    }
}