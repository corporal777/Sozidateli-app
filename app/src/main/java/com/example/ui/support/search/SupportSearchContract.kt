package com.example.ui.support.search

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SupportData
import com.example.ui.base.BaseContract

interface SupportSearchContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setQuestions(data: List<SupportData>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSupportQuestionAnswer(data: SupportData)

    }
    interface Presenter : BaseContract.Presenter {
        fun onQuestionClick(question: String)
        fun onSearchTextChange(text: String?)
        fun onSearchTextSubmit(text: String?)
    }
}