package com.example.ui.event.about

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.EventInfo
import com.example.data.models.EventPage
import com.example.data.models.Partner
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

    }

    override fun onPageClick(page: EventPage) {

    }

    override fun onPartnerClick(partner: Partner) {
        viewState.showPartner(partner)
    }

    override fun onContactsClick() {

    }

    override fun onGoToEventClick() {

    }
}
