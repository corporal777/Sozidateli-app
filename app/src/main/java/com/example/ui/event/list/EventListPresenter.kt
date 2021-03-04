package com.example.ui.event.list

import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.*
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
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.net.UnknownHostException

abstract class EventListPresenter<V : EventListContract.View>(
        private val appData: AppData,
        private val eventData: UserEventData,
        private val eventRepository: EventRepository,
        private val userRepository: UserRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<V>(), EventListContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    private val pagination: PaginationDataSourceFactory<EventNew?> = PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<EventNew?>

    private var isFirstAttach = true

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
                                            viewState.setData(it)
                                        }
                                )
                        //viewState.setData(it)
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
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }

    override fun onActionRegister(event: String) {
        compositeDisposable += eventRepository.eventRegisterCheck(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            checkRegistrationFields(event, emptyList())
                        },
                        onSuccess = {
                            checkRegistrationFields(event, it)
                        }
                )
    }

    private fun checkRegistrationFields(event: String, fields: List<EventRegisterCheckField>) {
        val filtered = fields.mapNotNull { it.title }
        if (fields.isEmpty()) {
            viewState.showEventRequest(event)
        } else {
            viewState.showRegistrationFieldsRequest(filtered)
        }
    }

    override fun onShowEditProfileClick() {
        viewState.showEditProfile(appData.getUser().user_id.toString())
    }

    override fun onActionCancel(event: String) {
        compositeDisposable += eventRepository.eventRegisterCancel(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    paginationList.invalidate()
                }
    }

    override fun onActionWriteToOrganization(emails: List<EventPhoneModel>) {
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: EventPhoneModel) {
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

    protected abstract fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew?>>
}
