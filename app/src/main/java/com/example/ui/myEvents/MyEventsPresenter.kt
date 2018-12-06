package com.example.ui.myEvents

import android.view.View
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
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

    override fun onEventClick(event: Event, vararg sharedElements: Pair<View, String>) {
        viewState.showAboutEvent(event, *sharedElements)
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }
}
