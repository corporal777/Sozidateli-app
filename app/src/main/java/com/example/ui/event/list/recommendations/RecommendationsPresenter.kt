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
    @Connectivity val connectivity: Observable<Boolean>
) : EventListPresenter<RecommendationsContract.View>(appData, eventRepository),
    RecommendationsContract.Presenter {

    private var isFirstAttach = true


    override fun attachView(view: RecommendationsContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(5) { null }, null)
        compositeDisposable += Observable.create(pagination)
            .map { transformData(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                else viewState.setData(it, appData.isNeedUpdateApp)
            }

        compositeDisposable += connectivity
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (hasNoConnectionError && it) {
                    hasNoConnectionError = false
                    pagination.invalidate()
                }
            }
    }

    override fun onSearchClick() = viewState.showSearch()
    override fun onRefreshRequest() = pagination.invalidate()
    override fun onItemTake(position: Int) = pagination.onItemTake(position)

    override fun onProfileClick() {
        val email = getUserData().personalEmail
        val phone = getUserData().personalPhone?.value
        if (email.isNullOrEmpty() || phone.isNullOrEmpty()) viewState.showUserProfile()
    }

    override fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
        Log.e("EventsList", "limit: $limit ,offset: $offset")
        return eventRepository.getEventsList(
            mapOf(
                EVENT_LIMIT to limit,
                EVENT_OFFSET to offset,
                EVENT_SORT_TYPE to "desc",
                EVENT_SORT_FIELD to "id",
                EVENT_BINDS to "userFavorite,user-registration,current-user-registration,current-user-registration-state,eventRegistrationState,format",
                EVENT_PUBLIC to "true",
                EVENT_STATUS to "approved,registration,registrationFinished,running"
            )
        )
    }
}