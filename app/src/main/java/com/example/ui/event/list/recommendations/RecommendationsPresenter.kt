package com.example.ui.event.list.recommendations

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.EventNew
import com.example.data.models.EventNew.Companion.EVENT_BINDS
import com.example.data.models.EventNew.Companion.EVENT_LIMIT
import com.example.data.models.EventNew.Companion.EVENT_OFFSET
import com.example.data.models.EventNew.Companion.EVENT_PUBLIC
import com.example.data.models.EventNew.Companion.EVENT_SORT_FIELD
import com.example.data.models.EventNew.Companion.EVENT_SORT_TYPE
import com.example.data.models.EventNew.Companion.EVENT_STATUS
import com.example.di.Connectivity
import com.example.extensions.buildListNew
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.flow.PaginationListFlow
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
    val appData: AppData,
    eventData: UserEventData,
    private val eventRepository: EventRepository,
    userRepository: UserRepository,
    @Connectivity val connectivity: Observable<Boolean>
) : BasePresenter<RecommendationsContract.View>(appData), RecommendationsContract.Presenter {

    private val pagination: PaginationDataSourceFactory<EventNew?> =
        PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationListFlow<EventNew?>
    private var isFirstAttach = true


    override fun attachView(view: RecommendationsContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventsList()
    }

    private fun getEventsList() {
        viewState.setData(List(10) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException)
                hasNoConnectionError = true
        }
            .buildListNew(enablePlaceholders = false)

        compositeDisposable += Flowable.create(paginationList, BackpressureStrategy.BUFFER)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty())
                    viewState.showEmptyListPlaceholder()
                else {
                    viewState.setData(it)
                }
            }

//        compositeDisposable += Observable.create(paginationList)
//            .performOnBackgroundOutOnMain()
//            .subscribeSimple {
//                if (it.isEmpty())
//                    viewState.showEmptyListPlaceholder()
//                else {
//                    viewState.apply {
//                        setData(it)
//                    }
//                }
//            }

        compositeDisposable += connectivity
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (hasNoConnectionError && it) {
                    hasNoConnectionError = false
                    paginationList.invalidate()
                }
            }
    }

    private fun getPaginationRequest(
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
                //EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState"/*,
                EVENT_BINDS to "userFavorite,user-registration,current-user-registration,current-user-registration-state,eventRegistrationState,format",
                EVENT_PUBLIC to "true",
                EVENT_STATUS to "approved,registration,registrationFinished,running"
            )
        )
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEventDetails(event))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                paginationList.invalidate()
            }
    }

    override fun onSearchClick() = viewState.showSearch()
    override fun onActionRegister(event: String) = viewState.showEventRequest(event)
    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onRefreshRequest() = paginationList.invalidate()
    override fun onItemTake(position: Int) = paginationList.onItemTake(position)

}