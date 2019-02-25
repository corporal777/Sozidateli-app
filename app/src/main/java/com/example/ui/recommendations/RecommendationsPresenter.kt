package com.example.ui.recommendations

import android.view.View
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.repository.DummyRepository
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<RecommendationsContract.View>(), RecommendationsContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        SimplePagination { limit, offset -> eventRepository.getEventList(limit, offset) }
                .build()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun attachView(view: RecommendationsContract.View?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onEventClick(event: Event, vararg sharedElements: Pair<View, String>) {
        viewState.showAboutEvent(event, *sharedElements)
    }

    override fun onGoToEventClick(event: Event) = viewState.showEventRequest(event)

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }
}
