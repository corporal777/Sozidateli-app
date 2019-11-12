package com.example.ui.event.about

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.*
import com.example.extensions.formatToInterval
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val userEventData: UserEventData
) : BasePresenter<AboutEventContract.View>(), AboutEventContract.Presenter {

    lateinit var eventId: String
    private lateinit var event: EventInfo

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()

        val userEventInfo = userEventData.userEvent?.eventInfo

        val eventInfoMaybe = if (userEventInfo?.event?.id == eventId) Maybe.just(userEventInfo)
        else eventRepository.getEventInfo(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
        compositeDisposable += eventInfoMaybe
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
                eventData.createMapInfo(),
                event.places
        )
    }

    private fun hasContacts(): Boolean {
        val eventData = event.event
        return eventData.name.isNotEmpty()
                || eventData.phone.isNotEmpty()
                || eventData.email.isNotEmpty()
                || eventData.web.isNotEmpty()
                || eventData.social.isNotEmpty()
                || eventData.address?.isNotEmpty() ?: false
                || eventData.createMapInfo() != null
                || event.places.isNotEmpty()
    }

    override fun onGoToEventClick() {
        viewState.showEventRequest(eventId)
    }

    override fun onLogoClick(url: String) {
        viewState.showLogoImage(url)
    }
}
