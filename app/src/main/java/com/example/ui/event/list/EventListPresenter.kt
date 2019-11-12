package com.example.ui.event.list

import com.example.data.models.Event
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationList
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import java.net.UnknownHostException

abstract class EventListPresenter<V : EventListContract.View>(
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<V>(), EventListContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    private val pagination: PaginationDataSourceFactory<Event?> = PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<Event?>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(20) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException)
                hasNoConnectionError = true
        }
                .buildList(enablePlaceholders = true)

        compositeDisposable += Observable.create(paginationList)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    viewState.setData(it)
                }

        compositeDisposable += connectivity
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (hasNoConnectionError && it) {
                        hasNoConnectionError = false
                        paginationList.invalidate()
                    }
                }
    }

    override fun attachView(view: V?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onEventClick(event: Event) {
        viewState.showAboutEvent(event.id)
    }

    override fun onGoToEventClick(event: Event) = viewState.showEventRequest(event)

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onItemTake(position: Int) {
        paginationList.onItemTake(position)
    }

    protected abstract fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>>
}
