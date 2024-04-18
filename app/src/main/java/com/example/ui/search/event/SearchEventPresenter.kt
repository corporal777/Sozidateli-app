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
    private val eventData: UserEventData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val commonRepository: CommonRepository,
    private val appData: AppData,
    private val socket: SocketIOManager
) : SearchPresenter<SearchEventContract.View, EventNew, SearchFilter.EventNew>(appData),
    SearchEventContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        val data = buildNewFilters(limit, offset)
        eventRepository.searchEventsNew(data)
    }

    private var isCommonDataLoaded = false
    private var interests: Map<InterestNew, List<InterestNew>>? = null
    private var formats: ArrayList<NewEventFormat> = arrayListOf()
    private var organizations: List<OrganizationNew>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val organizations = organizationRepository.getOrganizationsWithActiveEvents()
        val interests = userRepository.getInterestsList(null)
            .map { i -> i.data.groupByNotNull { child -> i.data.firstOrNull { it.id == child.parent } } }
        val eventFormats = eventRepository.getActiveEventFormatsList()

        compositeDisposable += Maybe.zip(interests, eventFormats, organizations) { i, f, o ->
            this.interests = i
            this.formats.apply { if (!f.isNullOrEmpty()) addAll(f) }
            this.organizations = o
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { isCommonDataLoaded = true },
                onSuccess = { isCommonDataLoaded = true }
            )
    }


    override fun onResume(searchInterface: SearchInterface) {
        super.onResume(searchInterface)
        searchInterface.apply {
            val initWithFilter = this.initWithFilter
            if (initWithFilter != null && initWithFilter is SearchFilter.EventNew) {
                tmpFilter = initWithFilter
                filter = initWithFilter
                this.initWithFilter = null
            }
        }
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
                    pagination.invalidate()
                }
            ).call(compositeDisposable)
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                pagination.invalidate()
            }.call(compositeDisposable)
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)


    override fun onShowFilterRequest() {
        val showFilter = {
            tmpFilter.interests = this.interests
            tmpFilter.formats = this.formats
            tmpFilter.organizations = this.organizations
            super.onShowFilterRequest()
        }
        if (isCommonDataLoaded) showFilter()
        else {
            compositeDisposable += Completable.complete()
                .timeout(3, TimeUnit.SECONDS)
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribe({
                    showFilter()
                }, {
                    showFilter()
                })
        }
    }

    override fun createFilter() = SearchFilter.EventNew()
    override fun copyFilter(filter: SearchFilter.EventNew) = filter.copy()
    override fun isHasFilter(): Boolean = filter.isHasFilter()
    override fun getSearchType(): String = SEARCH_EVENT_TYPE

    private fun buildNewFilters(limit: Int, offset: Int): MutableMap<String, Any> {
        Log.e("SearchEventsList", "limit: $limit ,offset: $offset")
        return mutableMapOf<String, Any>().apply {
            put(EventNew.EVENT_LIMIT, limit)
            put(EventNew.EVENT_OFFSET, offset)

            put(SEARCH_EVENT_TYPE, true)

            //if (searchText.isNotEmpty()) put(EventNew.EVENT_SEARCH, "%$searchText%")
            if (searchText.isNotEmpty()) put(EventNew.EVENT_SEARCH, searchText)

            val binds =
                "userFavorite,user-registration,current-user-registration,current-user-registration-state,eventRegistrationState"
            put(SEARCH_EVENT_BINDS, binds)

            val name = filter.name
            if (!name.isNullOrEmpty()) put(SEARCH_EVENT_NAME, "%$name%")

            if (filter.format != null) put(EventNew.EVENT_FORMAT, filter.format!!)
            if (filter.format == null && !filter.customFormat.isNullOrBlank())
                put(SEARCH_EVENT_FORMAT_CUSTOM, filter.customFormat!!)

            if (filter.organizationId != null)
                put(SEARCH_EVENT_ORGANIZATION, filter.organizationId!!)

            if (filter.organizationId == null && !filter.organizationName.isNullOrBlank())
                put(SEARCH_ORG_NAME, filter.organizationName!!)

            if (filter.dateStart != null)
                put(EventNew.EVENT_START_DATE, filter.dateStart + "," + filter.dateFinish)

            //address
            if (!filter.addressRegion.isNullOrEmpty()) {
                put(EventNew.EVENT_ADDRESS_REGION, filter.addressRegion!!)
            }
            if (!filter.addressTown.isNullOrEmpty()) {
                put(EventNew.EVENT_ADDRESS_CITY, filter.addressTown!!)
            }
            if (!filter.addressTownType.isNullOrEmpty()) {
                put("type", filter.addressTownType!!)
            }

            val topicCategory = filter.theme
            if (topicCategory != null) put(SEARCH_EVENT_TOPIC_CATEGORY, topicCategory)
            val topicSubcategory = filter.spec
            if (topicSubcategory != null) put(SEARCH_EVENT_TOPIC_SUBCATEGORY, topicSubcategory)
        }
    }

    companion object {
        private const val SEARCH_EVENT_TYPE = "event"
        private const val SEARCH_EVENT_NAME = "eventName"
        private const val SEARCH_EVENT_INTERESTS = "interests"
        private const val SEARCH_EVENT_TOPIC_CATEGORY = "topicCategory"
        private const val SEARCH_EVENT_TOPIC_SUBCATEGORY = "topicSubcategories"
        private const val SEARCH_EVENT_BINDS = "eventBinds"
        private const val SEARCH_EVENT_ORGANIZATION = "organization"
        private const val SEARCH_EVENT_FORMAT_CUSTOM = "formatCustom"
        private const val SEARCH_ORG_NAME = "orgName"
    }
}