package com.example.ui.favoritesTab.events

import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventFavoriteModel
import com.example.data.models.EventNew
import com.example.data.models.EventUserFavorite
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class FavoriteEventsPresenter
@Inject constructor(
    val appData: AppData,
    private val eventRepository: EventRepository,
) : BasePresenter<FavoriteEventsContract.View>(appData), FavoriteEventsContract.Presenter {

    private lateinit var paginationList: PaginationList<EventNew>

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }

    private fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew>> {
        return eventRepository.getEventFavoritesList(
            mutableMapOf<String, Any>().apply {
                put(EventFavoriteModel.EVENT_FAVORITE_LIMIT, limit)
                put(EventFavoriteModel.EVENT_FAVORITE_OFFSET, offset)
                put(EventFavoriteModel.EVENT_FAVORITE_TYPE, EventFavoriteModel.EVENT_TYPE)
                put(EventFavoriteModel.EVENT_FAVORITE_LOAD_MODEL, true)
                put(EventFavoriteModel.EVENT_FAVORITE_USER, appData.getId())
                put(EventFavoriteModel.EVENT_FAVORITE_BINDS, "userFavoriteActivities")
            }
        )
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(10) { null })

        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException) hasNoConnectionError = true
        }.buildFlow(enablePlaceholders = false, initialSize = 30)

        compositeDisposable += Observable.create(paginationList)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                else viewState.setData(it)
            }
    }

    override fun onShowEventClick(event: String?) {
        if (!event.isNullOrEmpty()) viewState.showAboutEvent(event)
    }

    override fun onEventActionClick(event: EventNew) {
        compositeDisposable += Completable.defer {
            if (event.binds?.userFavorite != null) {
                eventRepository.deleteFromFavorites(event.binds?.userFavorite?.id.toString())
                    .doOnComplete { event.binds?.userFavorite = null }
            } else {
                eventRepository.addEventToFavorites(event.id.toString())
                    .doOnSuccess { event.binds?.userFavorite = EventUserFavorite(it.id, it.user) }
                    .ignoreElement()
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    //paginationList.invalidate()
                    if (event.binds?.userFavorite != null) showAddedToFavoriteDialog()
                    else showRemovedFromFavoriteDialog()
                }
            }
    }

    override fun onEventSubEventsClick(event: EventNew) {
        viewState.showSubEvents(event.id.toString(), event.binds?.activity ?: emptyList())
    }

    override fun onItemTake(position: Int) = paginationList.onItemTake(position)
    override fun onRefreshRequest() = paginationList.invalidate()

    private fun addToFavoriteBody(id: Int?): AddToFavoriteModel {
        return AddToFavoriteModel(
            appData.getId(),
            AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_EVENT, id)
        )
    }
}