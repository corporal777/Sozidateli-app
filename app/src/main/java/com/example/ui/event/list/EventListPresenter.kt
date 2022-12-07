package com.example.ui.event.list

import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.*
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.net.UnknownHostException
import kotlin.math.abs

abstract class EventListPresenter<V : EventListContract.View>(
        private val appData: AppData,
        private val eventData: UserEventData,
        private val eventRepository: EventRepository,
        private val userRepository: UserRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<V>(appData), EventListContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0
    private var mDy = 0f

    private val pagination: PaginationDataSourceFactory<EventNew?> = PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<EventNew?>

    private var isFirstAttach = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        viewState.setData(List(10) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException)
                hasNoConnectionError = true
        }
                .buildList(enablePlaceholders = false)

        compositeDisposable += Observable.create(paginationList)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                    else {
                        compositeDisposable += eventRepository.getEventFormatsList(mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0))
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
                                )
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

    override fun attachView(view: V?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    override fun onActionRegister(event: String) {
        viewState.showEventRequest(event)
    }

    private fun checkRegistrationFields(event: String, fields: List<UserProfileFields>) {
        val filtered = fields.filter { it.filled == false }.mapNotNull { it.name }
        //val filtered = fields.mapNotNull { it.title }
        if (filtered.isEmpty()) {
            viewState.showEventRequest(event)
        } else {
            viewState.showRegistrationFieldsRequest(filtered)
        }
    }

    override fun onShowEditProfileClick() {
        viewState.showEditProfile(appData.getUser().user_id.toString())
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt()?: 0)
                .andThen(eventRepository.getEventDetails(event))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    paginationList.invalidate()
                }
    }


    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onShowFilterClick(format: Int) = viewState.showSearch(format)

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onItemTake(position: Int) {
        paginationList.onItemTake(position)
    }

    override fun onRefreshRequest() {
        paginationList.invalidate()
    }

    protected abstract fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew/*Event*/?>>
}
