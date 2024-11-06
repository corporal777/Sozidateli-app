package com.example.ui.search.event

import android.util.Log
import call
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.*
import com.example.data.socket.SocketIOManager
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.repository.UserRepository
import com.example.ui.search.SearchInterface
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SearchEventPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val appData: AppData,
    private val socket: SocketIOManager
) : SearchPresenter<SearchEventContract.View, SearchFilter.EventNew>(appData),
    SearchEventContract.Presenter {

    private var eventFilter = SearchFilter.EventNew()

//    override val pagination = PaginationDataSourceFactory { limit, offset ->
//        val data = buildNewFilters(limit, offset)
//        eventRepository.searchEvents(data) as Maybe<PaginationResponse<Any>>
//    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun onActionRegister(event: String, url: String?, formEnabled: Boolean) {
        if (url.isNullOrEmpty()) registerToEvent(event, formEnabled)
        else compositeDisposable += eventRepository.checkRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    if (it.isAccepted()) registerToEvent(event, formEnabled)
                    else viewState.showAgreementRegisterDialog(event, url, formEnabled)
                }
            )
    }

    override fun onAcceptRegistrationAgreement(event: String, formEnabled: Boolean) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) registerToEvent(event, formEnabled) }
            )
    }

    private fun registerToEvent(event: String, formEnabled: Boolean) {
        if (formEnabled) viewState.showEventRequest(event)
        else eventRepository.registerToEvent(event.toInt())
            .andThen(socket.connectToUpdates())
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    viewState.showEventRegistrationSuccessDialog()
                    //pagination.invalidate()
                }
            ).call(compositeDisposable)
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                //pagination.invalidate()
            }.call(compositeDisposable)
    }


    override fun onShowAuthorization(event: String) {
        appData.savedEventId = event
        viewState.showAuthorization()
    }

    override fun onFiltersApplyClick(filter: SearchFilter.EventNew) {
        eventFilter = filter
        viewState.setHasFilter()
        //pagination.invalidate()
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onShowFilterRequest() = viewState.showFilter(eventFilter)

    override fun onRefreshRequest() {}

    override fun isHasFilter(): Boolean = eventFilter.isHasFilter()


    private fun buildNewFilters(limit: Int, offset: Int): MutableMap<String, Any> {
        Log.e("SearchEventsList", "limit: $limit ,offset: $offset")
        return mutableMapOf<String, Any>().apply {
            put(EventNew.EVENT_LIMIT, limit)
            put(EventNew.EVENT_OFFSET, offset)

            put(SEARCH_EVENT_TYPE, true)

            if (searchText.isNotEmpty()) put(EventNew.EVENT_SEARCH, searchText)

            val binds = "user-registration,current-user-registration,current-user-registration-state,eventRegistrationState"
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