package com.example.ui.support.detail

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SupportData
import com.example.ui.base.BaseContract

interface SupportQuestionDetailContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setQuestion(title : String, answer: String)

    }
    interface Presenter : BaseContract.Presenter {
    }
}