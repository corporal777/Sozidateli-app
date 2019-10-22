package com.example.ui.event.about

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.*
import com.example.extensions.formatToDefaultDate
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
                    "${event.conferenceStart?.formatToDefaultDate()} - ${event.conferenceFinish?.formatToDefaultDate()}",
                    event.description,
                    eventInfo.pages,
                    eventInfo.partners
            )
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
        val event = event.event
        viewState.showContacts(
                event.name,
                event.phone,
                event.email,
                event.web,
                event.social,
                event.address,
                createMapInfo(event)
        )
    }

    private fun createMapInfo(event: EventData): MapInfo? {
        // TODO remove test data

        val lat = event.placeLat ?: 55.753754
        val lon = event.placeLon ?: 37.619667
        val title = null ?: "Как добраться"
        val description = event.placeHowToGet ?: (0..1000).joinToString("")

        return if ((lat == null || lon == null) && description == null) null
        else MapInfo(lat, lon, title, description)
    }

    override fun onGoToEventClick() {

    }
}
