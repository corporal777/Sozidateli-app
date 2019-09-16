package com.example.ui.event.list

import android.view.View
import com.example.data.models.Event
import com.example.data.models.EventApprove
import com.example.extensions.build
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.applyErrorHandler
import io.reactivex.rxkotlin.plusAssign
import withLoadingDialog

abstract class EventListPresenter<V : EventListContract.View> : BasePresenter<V>(), EventListContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    protected abstract val pagination: PaginationDataSourceFactory<EventApprove>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += pagination
                .applyErrorHandler {
                    it.printStackTrace()
                }
                .build()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
    }

    override fun attachView(view: V?) {
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
