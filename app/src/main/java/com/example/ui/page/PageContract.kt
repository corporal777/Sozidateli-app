package com.example.ui.page

import com.example.data.models.FileModel
import com.example.ui.base.bottomSheet.BaseBSContract
import moxy.viewstate.strategy.alias.OneExecution

interface PageContract {
    interface View : BaseBSContract.View {
        @OneExecution
        fun setContent(
                logo: String?,
                contentTitle: String?,
                title: String?,
                content: String?,
                documents: List<FileModel>?
        )

        @OneExecution
        fun openLinkInBrowser(link: String)
    }

    interface Presenter : BaseBSContract.Presenter {
        fun onDocumentClick(document: FileModel)
    }
}
