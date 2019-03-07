package com.example.ui.documents

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Document
import com.example.ui.base.BaseContract

interface DocumentsListContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(documents: PagedList<Document>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openLinkInBrowser(link:String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onDocumentClick(document: Document)
    }
}
