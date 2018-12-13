package com.example.ui.aboutEvent

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Event
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<AboutEventContract.View>(), AboutEventContract.Presenter {

    private lateinit var event: Event

    private var isUserEvent = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setEventData(event)
            setLabel(event.name)
        }
    }

    override fun onAboutForumClick() = viewState.showAboutForum(event)

    override fun onNewsClick() = viewState.showNews(event)

    override fun onDocumentsClick() = viewState.showDocuments(event)

    override fun onContactsClick() = viewState.showContacts(event)

    override fun onTransferClick() = viewState.showTransfer(event)

    override fun onGoToEventClick() = viewState.showEventRequest(event)

    fun setEvent(event: Event) {
        this.event = event
    }

    fun setupWithUserEvent() {
        isUserEvent = true
        setEvent(appData.event!!)
    }
}
