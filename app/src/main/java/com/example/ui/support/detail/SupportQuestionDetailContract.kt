package com.example.ui.support.detail

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface SupportQuestionDetailContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setQuestion(title : String, answer: String)

    }
    interface Presenter : BaseContract.Presenter {
    }
}