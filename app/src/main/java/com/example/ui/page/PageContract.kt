package com.example.ui.page

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Document
import com.example.data.models.FileModel
import com.example.ui.base.BaseContract

interface PageContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setContent(
                logo: String?,
                contentTitle: String,
                title: String?,
                content: String?,
                documents: List<FileModel>?
        )

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openLinkInBrowser(link: String)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun onDocumentClick(document: FileModel)
    }
}
