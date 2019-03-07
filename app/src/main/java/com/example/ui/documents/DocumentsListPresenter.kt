package com.example.ui.documents

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Document
import com.example.data.models.Event
import com.example.repository.DummyRepository
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class DocumentsListPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<DocumentsListContract.View>(), DocumentsListContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()
        SimplePagination { limit, offset -> eventRepository.getEventDocuments(event.event_id.toInt(), limit, offset) }
                .build()
                .subscribe({ viewState.apply { setData(it) } }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun onDocumentClick(document: Document) {
        document.file?.let {
            viewState.openLinkInBrowser(it)
        }
    }
}
