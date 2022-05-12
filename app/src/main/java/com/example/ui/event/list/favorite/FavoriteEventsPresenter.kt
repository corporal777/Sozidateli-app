package com.example.ui.event.list.favorite

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class FavoriteEventsPresenter
@Inject constructor(
        val appData: AppData,
        eventData: UserEventData,
        private val eventRepository: EventRepository,
        userRepository: UserRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<FavoriteEventsContract.View>(appData, eventData, eventRepository, userRepository, connectivity), FavoriteEventsContract.Presenter {

    /*override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return eventRepository.getFavoriteEvents(limit, offset)
    }*/
    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew?>> {
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
        //TODO Finish this screen
        //return eventRepository.getEventsList(mapOf(EventNew.EVENT_LIMIT to limit, EventNew.EVENT_OFFSET to offset))
    }

    override fun onEventActionClick(event: /*Event*/EventNew) {
        if (event.binds?.userFavorite != null) {
            compositeDisposable += eventRepository.deleteFromFavorite(event.binds?.userFavorite?.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        event.binds?.userFavorite = null
                        viewState.updateEventFavorite(event.id.toString(), false)
                        onRefreshRequest()
                    }
        } else {
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_EVENT, event.id?:0)))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        event.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.updateEventFavorite(event.id.toString(), true)
                        onRefreshRequest()
                    }
        }
        /*val isFavorite = event.isInFavorites
        val request = if (isFavorite) eventRepository.removeFromFavorite(event.id)
        else eventRepository.addToFavorite(event.id)

        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    event.isInFavorites = !isFavorite
                    viewState.updateEventFavorite(event.id, !isFavorite)
                    onRefreshRequest()
                }*/
    }

    override fun onEventSubeventsClick(event: /*Event*/EventNew) {
        viewState.showSubEvents(event.id.toString(), event.binds?.activity ?: emptyList())
    }
}