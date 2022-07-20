package com.example.ui.page

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Document
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
    private var mDy = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        compositeDisposable += eventRepository.getPageDetails(dataPageId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.setContent(/*it.picture*/"", it.name?: "", it.title, it.content, it.files)
                }
    }

    override fun attachView(view: PageContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    override fun onDocumentClick(document: FileModel) {
        document.let { viewState.openLinkInBrowser(it.uri?: "") }
    }
}
