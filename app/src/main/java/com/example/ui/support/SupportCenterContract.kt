package com.example.ui.support

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SupportData
import com.example.ui.base.BaseContract

interface SupportCenterContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setQuestions(questions: Map<String, List<SupportData>>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSupportQuestionAnswer(data: SupportData)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSupportSearch()
    }
    interface Presenter : BaseContract.Presenter {
        fun onQuestionClick(data: SupportData)
        fun onSearchClick()
    }
}