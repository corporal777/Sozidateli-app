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

        @StateStrategyType(SkipStrategy::class)
        fun showAddEmailDialog()

        @StateStrategyType(SkipStrategy::class)
        fun hideAddEmailDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showEmailIsNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailConfirmation(email: String)
    }
    interface Presenter : BaseMaxStateContract.Presenter {
        fun onSaveWorkClick(data: WorkExperienceServerModel)
        fun checkEmailIsUnique(email: String)
        fun onShowEmailConfirm(email : String)
    }
}