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
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
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
    @Connectivity val connectivity: Observable<Boolean>
) : EventListPresenter<RecommendationsContract.View>(appData, eventRepository, socket),
    RecommendationsContract.Presenter {

    private var isFirstAttach = true


    override fun attachView(view: RecommendationsContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()

        viewState.setAuthorizationButton(isTemporaryUser())
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(5) { null }, null)
        compositeDisposable += Observable.create(pagination)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                else viewState.setData(it, appData.isNeedUpdateApp)
            }
    }

    override fun onSearchClick() = viewState.showSearch()
    override fun onAuthorizationClick() {
        appData.savedEventId = null
        viewState.showAuthorization()
    }

    override fun onRefreshRequest() = pagination.invalidate()
    override fun onItemTake(position: Int) = pagination.onItemTake(position)


    override fun onShowSavedEventOrProfile(isProfile: Boolean?) {
        val eventId = appData.savedEventId
        if (!eventId.isNullOrEmpty()) {
            viewState.showAboutEvent(eventId)
            appData.savedEventId = null
        } else if (isProfile == true) {
            val email = getUserData().personalEmail
            val phone = getUserData().personalPhone?.value
            if (email.isNullOrEmpty() || phone.isNullOrEmpty()) viewState.showUserProfile()
        } else return
    }

    override fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
        Log.e("EventsList", "limit: $limit ,offset: $offset")
        return eventRepository.getEventsListNew(
            mapOf(
                EVENT_LIMIT to limit,
                EVENT_OFFSET to offset,
                EVENT_SORT_TYPE to "desc",
                EVENT_SORT_FIELD to "id",
                EVENT_BINDS to getBinds(),
                EVENT_PUBLIC to "true",
                EVENT_STATUS to "approved,registration,registrationFinished,running"
            )
        )
    }

    override fun getBinds(): String {
        return "current-user-registration,current-user-registration-state,eventRegistrationState"
    }
}