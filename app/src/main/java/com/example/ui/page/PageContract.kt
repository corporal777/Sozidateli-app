package com.example.ui.page

import com.example.data.models.FileModel
import com.example.ui.base.BaseContract
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface PageContract {
    interface View : BaseBottomSheetContract.View {
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

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onDocumentClick(document: FileModel)
    }
}
