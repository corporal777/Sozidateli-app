package com.example.ui.event.list

import com.example.data.UserEventData
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationList
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.net.UnknownHostException

abstract class EventListPresenter<V : EventListContract.View>(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository,
        private val userRepository: UserRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<V>(), EventListContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    private val pagination: PaginationDataSourceFactory<Event?> = PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<Event?>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(20) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException)
                hasNoConnectionError = true
        }
                .buildList(enablePlaceholders = true)

        compositeDisposable += Observable.create(paginationList)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                    else viewState.setData(it)
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
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onActionRegister(event: String) = viewState.showEventRequest(event)

    override fun onActionCancel(event: String) {
        compositeDisposable += eventRepository.eventRegisterCancel(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    paginationList.invalidate()
                }
    }

    override fun onActionWriteToOrganization(emails: List<EmailAffiliation>) {
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: EmailAffiliation) {
        viewState.showWriteToOrganization(email)
    }

    override fun onActionShowEvent(event: String) {
        compositeDisposable += eventRepository.setDefaultEvent(event)
                .andThen(userRepository.getUserShort().ignoreElement().onErrorComplete())
                .andThen(eventData.load(event))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }
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

    protected abstract fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>>
}
