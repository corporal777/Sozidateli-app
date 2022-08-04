package com.example.ui.event.list.recommendations

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.EventNew.Companion.EVENT_BINDS
import com.example.data.models.EventNew.Companion.EVENT_HIDDEN
import com.example.data.models.EventNew.Companion.EVENT_LIMIT
import com.example.data.models.EventNew.Companion.EVENT_OFFSET
import com.example.data.models.EventNew.Companion.EVENT_SORT_TYPE
import com.example.data.models.EventNew.Companion.EVENT_STATUS
import com.example.data.models.EventNewModel
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.list.EventListPresenter
import com.example.ui.views.StateType
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationList
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
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

    private var scrollPosition = 0
    private var scrollOffset = 0

    private val pagination: PaginationDataSourceFactory<EventNew?> = PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<EventNew?>
    private var isFirstAttach = true


    override fun attachView(view: RecommendationsContract.View?) {
        super.attachView(view)
        //viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventsList()
    }

    private fun getEventsList(){
        viewState.setData(List(10) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException)
                hasNoConnectionError = true
        }
            //.buildList(enablePlaceholders = true)
            .buildList(enablePlaceholders = false)

        compositeDisposable += Observable.create(paginationList)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                else {
                    viewState.setData(it)
                    /*compositeDisposable += eventRepository.getEventFormatsList(mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0))
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(
                            onError = { error ->
                                viewState.setData(it)
                            },
                            onSuccess = { formats ->
                                it.forEach { event ->
                                    event?.format?.name = formats?.firstOrNull { f -> f.id == event?.format?.value }?.name
                                }
                                viewState.setData(it.filterNotNull())
                            }
                        )*/
                }
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

    private fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew?>> {
        Log.e("EventsList", "limit: $limit ,offset: $offset")
        return eventRepository.getEventsList(mapOf(EVENT_LIMIT to limit, EVENT_OFFSET to offset, /*EVENT_SORT_TYPE to "desc",*/
            //EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState"/*,
            EVENT_BINDS to "organization,user-registration,current-user-registration,eventRegistrationState"/*,
                EVENT_STATUS to "approved,registration,running"*/, EVENT_HIDDEN to false, EVENT_STATUS to "registration,running,registrationFinished,approved"))
    }


    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }


    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt()?: 0)
            .andThen(eventRepository.getEventDetails(event))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            //.withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                paginationList.invalidate()
            }
    }

    override fun onActionShowEvent(event: String) {}
    override fun onSearchClick() = viewState.showSearch()
    override fun onActionRegister(event: String) { viewState.showEventRequest(event) }
    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onRefreshRequest() = paginationList.invalidate()
    override fun onItemTake(position: Int) = paginationList.onItemTake(position)

}