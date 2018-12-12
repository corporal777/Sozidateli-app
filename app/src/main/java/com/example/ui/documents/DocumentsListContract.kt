package com.example.ui.documents

import android.arch.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Document
import com.example.ui.base.BaseContract

interface DocumentsListContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(documents: PagedList<Document>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onDocumentClick(document: Document)
    }
}
