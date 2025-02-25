package com.example.ui.favoritesTab.events

import com.example.data.AppData
import com.example.data.models.EventFavoriteModel
import com.example.data.models.EventNew
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class FavoriteEventsPresenter
@Inject constructor(
    val appData: AppData,
    private val eventRepository: EventRepository,
) : BasePresenter<FavoriteEventsContract.View>(appData), FavoriteEventsContract.Presenter {


    private val pagination = PagingDataSourceFactory { limit, offset ->
        eventRepository.getEventFavoritesList(
            mutableMapOf<String, Any>().apply {
                put(EventFavoriteModel.EVENT_FAVORITE_LIMIT, limit)
                put(EventFavoriteModel.EVENT_FAVORITE_OFFSET, offset)
                put(EventFavoriteModel.EVENT_FAVORITE_TYPE, EventFavoriteModel.EVENT_TYPE)
                put(EventFavoriteModel.EVENT_FAVORITE_LOAD_MODEL, true)
                put(EventFavoriteModel.EVENT_FAVORITE_USER, appData.getId())
                //put(EventFavoriteModel.EVENT_FAVORITE_BINDS, "userFavoriteActivities")
            })
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 30, distance = 5)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) })
    }


    override fun onEventActionClick(event: EventNew) {
        compositeDisposable += eventRepository.deleteFromFavorites(event.binds?.userFavorite?.id.toString())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.updateEvent(event)
                },
                onComplete = {
                    pagination.invalidateStart()
                    viewState.showRemovedFromFavoriteDialog()
                })
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onRefreshRequest() = pagination.invalidate()
}