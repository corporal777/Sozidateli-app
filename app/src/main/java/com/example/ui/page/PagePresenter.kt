package com.example.ui.page

import com.example.data.AppData
import com.example.data.models.FileModel
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class PagePresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    appData: AppData
) : BaseBottomSheetPresenter<PageContract.View>(appData), PageContract.Presenter {

    lateinit var dataEventId: String
    lateinit var dataPageId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getPageDetails(dataPageId)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
            .subscribeSimple {
                viewState.setContent("", it.name, it.title, it.content, it.files)
            }
    }

    override fun onDocumentClick(document: FileModel) {
        document.let { viewState.openLinkInBrowser(it.uri ?: "") }
    }
}
