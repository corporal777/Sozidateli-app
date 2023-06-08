package com.example.ui.event.list.favorite

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventFavoriteModel
import com.example.data.models.EventNew
import com.example.data.models.EventUserFavorite
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class FavoriteEventsPresenter
@Inject constructor(
    val appData: AppData,
    private val eventRepository: EventRepository,
) : BasePresenter<FavoriteEventsContract.View>(appData), FavoriteEventsContract.Presenter {

    private val pagination: PaginationDataSourceFactory<EventNew?> =
        PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<EventNew?>

    private fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
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
        }.buildList(enablePlaceholders = false, initialSize = 30)

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

    override fun onEventActionClick(event: /*Event*/EventNew) {
        if (event.binds?.userFavorite != null) {
            compositeDisposable += eventRepository.deleteFromFavorite(event.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    event.binds?.userFavorite = null
                    viewState.updateEventFavorite(event.id.toString(), false)
                    paginationList.invalidate()
                }
        } else {
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_EVENT, event.id ?: 0)
                )
            )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    event.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.updateEventFavorite(event.id.toString(), true)
                    paginationList.invalidate()
                }
        }
    }

    override fun onEventSubEventsClick(event: EventNew) {
        viewState.showSubEvents(event.id.toString(), event.binds?.activity ?: emptyList())
    }

    override fun onItemTake(position: Int) = paginationList.onItemTake(position)
    override fun onRefreshRequest() = paginationList.invalidate()
}