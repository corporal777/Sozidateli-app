package com.example.ui.mapTabs

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MapTabsPresenter
@Inject constructor(
        private val userEventData: UserEventData,
        private val eventRepository: EventRepository
) : BasePresenter<MapTabsContract.View>(), MapTabsContract.Presenter {

    private var selectedPagePosition = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if(userEventData.event==null)return
        eventRepository.getEventMapInfo(userEventData.event?.id!!)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    userEventData.mapInfo=it
                    viewState.initPages()
                },{})
                .call(compositeDisposable)
    }

    override fun attachView(view: MapTabsContract.View?) {
        super.attachView(view)
        viewState.selectPageAtPosition(selectedPagePosition)
    }

    override fun onPageSelected(position: Int) {
        selectedPagePosition = position
    }
}
