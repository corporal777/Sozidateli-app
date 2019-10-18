package com.example.ui.documents

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Document
import com.example.ui.base.BaseContract

interface DocumentsListContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(documents: List<Document>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTitle(title: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openLinkInBrowser(link: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onDocumentClick(document: Document)
    }
}
