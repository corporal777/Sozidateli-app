package com.example.ui.event.list

import com.example.data.models.Event
import com.example.extensions.buildList
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationList
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog

abstract class EventListPresenter<V : EventListContract.View> : BasePresenter<V>(), EventListContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    protected abstract val pagination: PaginationDataSourceFactory<Event>
    private lateinit var paginationList: PaginationList<Event>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        paginationList = pagination.applyErrorHandler {
            it.printStackTrace()
        }
                .buildList()

        compositeDisposable += Observable.create(paginationList)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
    }

    override fun attachView(view: V?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onEventClick(event: Event) {
        viewState.showAboutEvent(event)
    }

    override fun onGoToEventClick(event: Event) = viewState.showEventRequest(event)

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onItemTake(position: Int) {
        paginationList.onItemTake(position)
    }
}
