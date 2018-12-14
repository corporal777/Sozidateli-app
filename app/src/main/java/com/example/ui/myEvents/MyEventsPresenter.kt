package com.example.ui.myEvents

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Event
import com.example.data.prefs.AppPrefs
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
        private val appData: AppData,
        private val appPrefs: AppPrefs,
        private val dummyRepository: DummyRepository
) : BasePresenter<MyEventsContract.View>(), MyEventsContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        SimplePagination { limit, offset -> dummyRepository.loadRecommendations(limit, offset) }
                .create()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun attachView(view: MyEventsContract.View?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onEventClick(event: Event) {
        appData.event = event
        appPrefs.selectedEvent = event.id
        viewState.selectEvent(event)
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }
}
