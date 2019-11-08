package com.example.ui.eventTabs

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class EventTabsPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository
) : BasePresenter<EventTabsContract.View>(), EventTabsContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            initialNavigationSetup()
            setLabel(eventData.event!!.name)
        }

        onMyScheduleTabSelected()
    }

    override fun onMyScheduleTabSelected() = viewState.showMyScheduleTab()

    override fun onScheduleTabSelected() = viewState.showScheduleTab()

    override fun onAboutSelected() {
        eventData.event?.id?.let { viewState.showAboutTab(it) }
    }

    override fun onMapTabsSelected() {
        eventData.event?.id?.let { viewState.showMapTab() }
    }

    override fun onToListSelected() {
        compositeDisposable += eventRepository.setDefaultEvent("0")
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    eventData.clear()
                    viewState.showEventList()
                }, {
                    it.printStackTrace()
                })
    }

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuAccountClick() = viewState.showAccount()

    override fun onDestroy() {
        super.onDestroy()
        eventData.clear()
    }
}
