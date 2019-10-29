package com.example.ui.event.about

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.*
import com.example.extensions.formatToInterval
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<AboutEventContract.View>(), AboutEventContract.Presenter {

    lateinit var eventId: String
    private lateinit var event: EventInfo

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()
        compositeDisposable += eventRepository.getEventInfo(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(onSuccess = ::setEventInfoData)
    }

    private fun setEventInfoData(eventInfo: EventInfo) {
        this.event = eventInfo
        val event = eventInfo.event
        viewState.apply {
            setEventName(event.name)
            setEventData(
                    event.logo,
                    event.organization?.name,
                    event.conferenceStart?.formatToInterval(event.conferenceFinish),
                    event.description,
                    eventInfo.pages,
                    eventInfo.partners,
                    hasContacts()
            )
            showRegisterButton(Event.isCanRegister(event.status, eventInfo.userRegistration?.status))
        }
    }

    override fun onSpeakersClick() {
        checkInternetAndRun { viewState.showSpeakers(eventId) }
    }

    override fun onPageClick(page: EventPage) {
        checkInternetAndRun {
            if (page.isFilePage) viewState.showDocuments(eventId, page.id.toString())
            else viewState.showPage(eventId, page.id.toString())
        }
    }

    override fun onPartnerClick(partner: EventParther) {
        checkInternetAndRun { viewState.showPartner(eventId, partner.id.toString()) }
    }

    override fun onContactsClick() {
        val eventData = event.event
        viewState.showContacts(
                eventData.name,
                eventData.phone,
                eventData.email,
                eventData.web,
                eventData.social,
                eventData.address,
                createMapInfo(eventData),
                event.places
        )
    }

    private fun createMapInfo(event: EventData): MapInfo? {
        val lat = event.placeLat
        val lon = event.placeLon
        val title = event.placeHowToGetTitle
        val description = event.placeHowToGet

        return if ((lat == null || lon == null) && description == null) null
        else MapInfo(lat, lon, title, description)
    }

    private fun hasContacts(): Boolean {
        val eventData = event.event
        return eventData.name.isNotEmpty()
                && eventData.phone.isNotEmpty()
                && eventData.email.isNotEmpty()
                && eventData.web.isNotEmpty()
                && eventData.social.isNotEmpty()
                && eventData.address?.isNotEmpty() ?: false
                && createMapInfo(eventData) != null
                && event.places.isNotEmpty()
    }

    override fun onGoToEventClick() {
        viewState.showEventRequest(eventId)
    }
}
