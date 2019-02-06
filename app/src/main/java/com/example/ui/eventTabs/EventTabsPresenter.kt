package com.example.ui.eventTabs

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.prefs.AppPrefs
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventTabsPresenter
@Inject constructor(
        private val appPrefs: AppPrefs,
        private val appData: AppData
) : BasePresenter<EventTabsContract.View>(), EventTabsContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setLabel(appData.event!!.name)
        }
    }

    override fun onMyScheduleTabSelected() = viewState.showMyScheduleTab()

    override fun onScheduleTabSelected() = viewState.showScheduleTab()

    override fun onAboutSelected() = viewState.showAboutTab()

    override fun onMapTabsSelected() = viewState.showMapTab()

    override fun onToListSelected() {
        appPrefs.selectedEvent = null
        appData.event = null
        viewState.showEventList()
    }

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuSearchClick() = viewState.showSearch()

    override fun onMenuAccountClick() = viewState.showAccount()
}
