package com.example.ui.aboutEvent

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(
) : BasePresenter<AboutEventContract.View>(), AboutEventContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setEventData(event)
            setChatLabel(event.name)
        }
    }

    override fun onAboutForumClick() = viewState.showAboutForum(event)

    override fun onNewsClick() = viewState.showNews(event)

    override fun onDocumentsClick() = viewState.showDocuments(event)

    override fun onContactsClick() = viewState.showContacts(event)

    override fun onTransferClick() = viewState.showTransfer(event)
}
