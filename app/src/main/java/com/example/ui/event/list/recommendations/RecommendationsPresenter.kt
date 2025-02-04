package com.example.ui.event.list.recommendations

import android.util.Log
import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.models.EventNew.Companion.EVENT_BINDS
import com.example.data.models.EventNew.Companion.EVENT_LIMIT
import com.example.data.models.EventNew.Companion.EVENT_OFFSET
import com.example.data.models.EventNew.Companion.EVENT_PUBLIC
import com.example.data.models.EventNew.Companion.EVENT_SORT_FIELD
import com.example.data.models.EventNew.Companion.EVENT_SORT_TYPE
import com.example.data.models.EventNew.Companion.EVENT_STATUS
import com.example.data.socket.SocketIOManager
import com.example.di.Connectivity
import com.example.exceptions.EmptyDataException
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenterNew
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.flow.PagingDataSourceFactory
import com.example.util.pagination.flow.PagingList
import com.example.util.pagination.flow.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
    val appData: AppData,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
) : EventListPresenterNew<RecommendationsContract.View>(appData, eventRepository, socket),
    RecommendationsContract.Presenter {

    var isOpenProfile = false

    override val pagination = PagingDataSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }.applyErrorHandler { onReceivePagingError(it) }.buildList(initialSize = 20, distance = 2)

    override fun attachView(view: RecommendationsContract.View) {
        super.attachView(view)
        viewState.setAuthorizationButton(isTemporaryUser())
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it, appData.isNeedUpdateApp) }
            )
        showSavedEventOrProfile()
    }

    override fun onSearchClick() = viewState.showSearch()


    private fun showSavedEventOrProfile() {
        val eventId = appData.savedEventId
        if (!eventId.isNullOrEmpty()) {
            viewState.showAboutEvent(eventId)
            appData.savedEventId = null
        } else if (isOpenProfile) {
            isOpenProfile = false
            val email = getUserData().personalEmail
            val phone = getUserData().personalPhone?.value
            if (email.isNullOrEmpty() || phone.isNullOrEmpty()) viewState.showUserProfile()
        }
    }

    override fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew>> {
        return eventRepository.getEventsListNew(
            mapOf(
                EVENT_LIMIT to limit,
                EVENT_OFFSET to offset,
                EVENT_SORT_TYPE to "desc",
                EVENT_SORT_FIELD to "id",
                EVENT_BINDS to "current-user-registration,current-user-registration-state,eventRegistrationState",
                EVENT_PUBLIC to "true",
                EVENT_STATUS to "approved,registration,registrationFinished,running"
            )
        )
    }
}