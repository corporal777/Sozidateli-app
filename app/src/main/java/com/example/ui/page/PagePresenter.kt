package com.example.ui.page

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FileModel
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class PagePresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        appData: AppData
) : BasePresenter<PageContract.View>(appData), PageContract.Presenter {

    lateinit var dataEventId: String
    lateinit var dataPageId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getPageDetails(dataPageId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.setContent(/*it.picture*/"", it.name?: "", it.title, it.content, it.files)
                }
    }

    override fun onDocumentClick(document: FileModel) {
        document.let { viewState.openLinkInBrowser(it.uri?: "") }
    }
}
