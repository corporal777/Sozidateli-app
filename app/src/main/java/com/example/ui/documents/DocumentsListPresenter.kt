package com.example.ui.documents

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Document
import com.example.data.models.Event
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class DocumentsListPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<DocumentsListContract.View>(), DocumentsListContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        SimplePagination { limit, offset -> dummyRepository.loadDocuments(event.id, limit, offset) }
                .create()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun onDocumentClick(document: Document) {

    }
}
