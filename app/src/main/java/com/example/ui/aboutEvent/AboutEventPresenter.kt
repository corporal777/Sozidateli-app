package com.example.ui.aboutEvent

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.data.models.Partner
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(private val userEventData: UserEventData,
        private val eventRepository: EventRepository) : BasePresenter<AboutEventContract.View>(), AboutEventContract.Presenter {

    private lateinit var event: Event

    private var isUserEvent = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            showLoadingDialog()
            setEventData(event)
            setVisibleButtonGoToEvent(!isUserEvent)
            event.name?.let {
                setLabel(it)
            }

            if (isUserEvent) {
                userEventData.partners?.let {
                    setPartners(it)
                }
            }
        }


    }

    override fun onImageLoad() {
        viewState.hideLoadingDialog()
    }

    override fun onImageLoadError() {
        viewState.hideLoadingDialog()
    }

    override fun onPartnerClick(partner: Partner) {
        viewState.showPartner(partner)
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
        setEvent(userEventData.event!!)
    }
}
