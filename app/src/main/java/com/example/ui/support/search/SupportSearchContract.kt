package com.example.ui.support.search

import com.example.data.models.SupportData
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface SupportSearchContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setQuestions(data: List<SupportData>)

        @OneExecution
        fun showSupportQuestionAnswer(data: SupportData)

    }
    interface Presenter : BaseContract.Presenter {
        fun onQuestionClick(question: String)
        fun onSearchTextChange(text: String?)
        fun onSearchTextSubmit(text: String?)
    }
}