package com.example.ui.eventTabs

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.createMapInfo
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.repository.UserRepository
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
        private val userRepository: UserRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<EventTabsContract.View>(), EventTabsContract.Presenter {

    private val userEvent = eventData.userEvent!!
    private var isInternetConnected = false
    private var isFirstAttach = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += connectivity
                .performOnBackgroundOutOnMain()
                .subscribeSimple(onNext = {
                    isInternetConnected = it
                    if (it) {
                        viewState.showNoConnectionMessage(false)
                        if (eventData.isDataFromLocalStorage) loadData()
                    }
                })

        onMyScheduleTabSelected()
    }

    override fun attachView(view: EventTabsContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else if (!isInternetConnected) {
            viewState.apply {
                showMyScheduleTab()
                showNoConnectionMessage(false)
            }
        }
    }

    private fun loadData() {
        compositeDisposable += eventData.load(userEvent.eventId)
                .performOnBackgroundOutOnMain()
                .subscribeSimple { }
    }

    override fun onMyScheduleTabSelected() {
        viewState.apply {
            showMyScheduleTab()
            showNoConnectionMessage(false)
        }
    }

    override fun onScheduleTabSelected() {
        viewState.apply {
            showScheduleTab()
            showNoConnectionMessage(false)
        }
    }

    override fun onAboutSelected() {
        val eventId = userEvent.eventId
        viewState.apply {
            showAboutTab(eventId)
            showNoConnectionMessage(!isInternetConnected)
        }
    }

    override fun onMapTabsSelected() {
        val eventInfo = userEvent.eventInfo
        viewState.apply {
            showMapTab(
                    eventInfo.event.name,
                    eventInfo.event.createMapInfo(),
                    eventInfo.places.toTypedArray()
            )
            showNoConnectionMessage(!isInternetConnected)
        }
    }

    override fun onToListSelected() {
        compositeDisposable += eventRepository.setDefaultEvent("0")
                .andThen(userRepository.getUserShort().ignoreElement().onErrorComplete())
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onComplete = {
                            eventData.clear()
                            viewState.showEventList()
                        },
                        onNoInternetConnectionException = {
                            viewState.showNoConnectionMessage(true)
                        })
    }

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuAccountClick() = viewState.showAccount()

    override fun onDestroy() {
        super.onDestroy()
        eventData.clear()
    }
}
