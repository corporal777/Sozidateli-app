package com.example.ui.eventTabs

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.createMapInfo
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class EventTabsPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<EventTabsContract.View>(), EventTabsContract.Presenter {

    private val userEvent = eventData.userEvent!!

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += connectivity
                .performOnBackgroundOutOnMain()
                .subscribeSimple(onNext = {
                    if (it && eventData.isDataFromLocalStorage) loadData()
                }, onError = {
                    it.printStackTrace()
                })

        viewState.apply {
            initialNavigationSetup()
            setLabel(userEvent.eventInfo.event.name)
        }

        onMyScheduleTabSelected()
    }

    private fun loadData() {
        compositeDisposable += eventData.load(userEvent.eventId)
                .performOnBackgroundOutOnMain()
                .subscribeSimple { }
    }

    override fun onMyScheduleTabSelected() = viewState.showMyScheduleTab()

    override fun onScheduleTabSelected() = viewState.showScheduleTab()

    override fun onAboutSelected() {
        val eventId = userEvent.eventId
        viewState.showAboutTab(eventId)
    }

    override fun onMapTabsSelected() {
        val eventInfo = userEvent.eventInfo
        viewState.showMapTab(
                eventInfo.event.name,
                eventInfo.event.createMapInfo(),
                eventInfo.places.toTypedArray()
        )
    }

    override fun onToListSelected() {
        compositeDisposable += eventRepository.setDefaultEvent("0")
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onComplete = {
                            eventData.clear()
                            viewState.showEventList()
                        },
                        onNoInternetConnectionException = {
                            viewState.showNoConnectionMessage()
                        })
    }

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuAccountClick() = viewState.showAccount()

    override fun onDestroy() {
        super.onDestroy()
        eventData.clear()
    }
}
