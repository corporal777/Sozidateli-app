package com.example.ui.support

import com.example.data.models.SupportData
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface SupportCenterContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setQuestions(questions: Map<String, List<SupportData>>)

        @OneExecution
        fun showSupportQuestionAnswer(data: SupportData)

        @OneExecution
        fun showSupportSearch()
    }
    interface Presenter : BaseContract.Presenter {
        fun onQuestionClick(data: SupportData)
        fun onSearchClick()
    }
}