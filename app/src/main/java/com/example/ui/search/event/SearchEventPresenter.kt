package com.example.ui.search.event

import android.util.Log
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.ui.search.SearchPresenter
import com.example.ui.views.dialogs.StateType
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withDelay
import withEventLoading
import javax.inject.Inject

@InjectViewState
class SearchEventPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val appData: AppData,
    private val socket: SocketIOManager
) : SearchPresenter<SearchEventContract.View>(appData), SearchEventContract.Presenter {

    private var eventFilter = SearchFilter.EventNew()

    private val pagination = PagingDataSourceFactory { limit, offset ->
        eventRepository.searchEvents(buildNewFilters(limit, offset))
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 20, distance = 3)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it, isTemporaryUser()) })
    }


    override fun onActionRegister(event: EventNew, withAccept: Boolean, position: Int) {
        if (isProfileLevelLow(event)) viewState.showStateErrorMessage(StateType.BASE, false, null)
        else compositeDisposable += eventRepository.acceptEventAgreement(event, withAccept)
            .flatMapMaybe { registerToEventRequest(event) }
            .performOnBackgroundOutOnMain()
            .withEventLoading(viewState, position)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    if (event.isFormEnabled()) viewState.showEventRequest(event.id.toString())
                    else {
                        viewState.showEventRegistrationSuccessDialog()
                        viewState.updateEvent(event)
                        appData.sendUpdateEvent(event)
                    }
                }
            )
    }

    override fun onActionCancel(event: EventNew, position: Int) {
        val registrationId = event.binds?.currentUserRegistration?.id ?: 0
        if (isProfileLevelLow(event)) viewState.showStateErrorMessage(StateType.BASE, false, null)
        else compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId)
            .andThen(eventRepository.getEvent(event.id.toString()))
            .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
            .performOnBackgroundOutOnMain()
            .withEventLoading(viewState, position)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.updateEvent(event)
                    appData.sendUpdateEvent(event)
                })
    }

    private fun registerToEventRequest(event: EventNew): Maybe<EventNew> {
        return Maybe.defer {
            if (event.isFormEnabled()) Maybe.just(event).withDelay(500)
            else eventRepository.registerToEvent(event.id ?: 0)
                //.andThen(socket.connectToUpdates())
                .andThen(eventRepository.getEvent(event.id.toString()))
                .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
        }
    }


    override fun onFiltersApplyClick(filter: SearchFilter.EventNew) {
        eventFilter = filter
        viewState.setHasFilter()
        pagination.invalidate()
    }

    override fun onShowAuthorization(event: String) {
        appData.savedEventId = event
        viewState.showAuthorization()
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onScanClick() = viewState.showQrScanner()

    override fun onShowFilterRequest() = viewState.showFilter(eventFilter)

    override fun onRefreshRequest() = pagination.invalidate()

    override fun isHasFilter(): Boolean = eventFilter.isHasFilter()


    private fun buildNewFilters(limit: Int, offset: Int): MutableMap<String, Any> {
        Log.e("SearchEventsList", "limit: $limit ,offset: $offset")
        return mutableMapOf<String, Any>().apply {
            put(EventNew.EVENT_LIMIT, limit)
            put(EventNew.EVENT_OFFSET, offset)

            put(SEARCH_EVENT_TYPE, true)

            if (searchText.isNotEmpty()) put(EventNew.EVENT_SEARCH, searchText)

            val binds =
                "current-user-registration,current-user-registration-state,eventRegistrationState"
            put(SEARCH_EVENT_BINDS, binds)

            val name = eventFilter.name
            if (!name.isNullOrEmpty()) put(SEARCH_EVENT_NAME, "%$name%")

            if (eventFilter.format != null) put(EventNew.EVENT_FORMAT, eventFilter.format!!)
            if (eventFilter.format == null && !eventFilter.customFormat.isNullOrBlank())
                put(SEARCH_EVENT_FORMAT_CUSTOM, eventFilter.customFormat!!)

            if (eventFilter.organizationId != null)
                put(SEARCH_EVENT_ORGANIZATION, eventFilter.organizationId!!)

            if (eventFilter.organizationId == null && !eventFilter.organizationName.isNullOrBlank())
                put(SEARCH_ORG_NAME, eventFilter.organizationName!!)

            if (eventFilter.dateStart != null)
                put(EventNew.EVENT_START_DATE, eventFilter.dateStart + "," + eventFilter.dateFinish)

            //address
            if (!eventFilter.addressRegion.isNullOrEmpty()) {
                put(EventNew.EVENT_ADDRESS_REGION, eventFilter.addressRegion!!)
            }
            if (!eventFilter.addressTown.isNullOrEmpty()) {
                put(EventNew.EVENT_ADDRESS_CITY, eventFilter.addressTown!!)
            }
            if (!eventFilter.addressTownType.isNullOrEmpty()) {
                put("type", eventFilter.addressTownType!!)
            }

            //interest
            val topicCategory = eventFilter.theme
            if (topicCategory != null) put(SEARCH_EVENT_TOPIC_CATEGORY, topicCategory)
            val topicSubcategory = eventFilter.spec
            if (topicSubcategory != null) put(SEARCH_EVENT_TOPIC_SUBCATEGORY, topicSubcategory)
        }
    }

    companion object {
        private const val SEARCH_PAGE_SIZE = 20
        private const val SEARCH_EVENT_TYPE = "event"
        private const val SEARCH_EVENT_NAME = "eventName"
        private const val SEARCH_EVENT_TOPIC_CATEGORY = "topicCategory"
        private const val SEARCH_EVENT_TOPIC_SUBCATEGORY = "topicSubcategories"
        private const val SEARCH_EVENT_BINDS = "eventBinds"
        private const val SEARCH_EVENT_ORGANIZATION = "organization"
        private const val SEARCH_EVENT_FORMAT_CUSTOM = "formatCustom"
        private const val SEARCH_ORG_NAME = "orgName"
    }
}