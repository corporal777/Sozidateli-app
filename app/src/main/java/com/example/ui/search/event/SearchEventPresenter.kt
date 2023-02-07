package com.example.ui.search.event

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBody.Companion.CALENDAR_EVENT
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.data.models.Event.Companion.FILTER_ADDRESS
import com.example.data.models.Event.Companion.FILTER_CATEGORY
import com.example.data.models.Event.Companion.FILTER_CONTENT
import com.example.data.models.Event.Companion.FILTER_DATE_FINISH
import com.example.data.models.Event.Companion.FILTER_DATE_START
import com.example.data.models.Event.Companion.FILTER_FORMAT
import com.example.data.models.Event.Companion.FILTER_NAME
import com.example.data.models.Event.Companion.FILTER_REGISTRATION
import com.example.data.models.Event.Companion.FILTER_SHOW_CANCELED
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.search.SearchInterface
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.processNextEventInCurrentThread
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SearchEventPresenter
@Inject constructor(
    private val eventData: UserEventData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository,
    private val appData: AppData
) : SearchPresenter<SearchEventContract.View, EventNew, SearchFilter.EventNew>(appData),
    SearchEventContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        Log.e("SearchEventsList", "limit: $limit ,offset: $offset")
        /*
        val data = mutableMapOf<String, Any>().apply {
            put(EventNew.EVENT_LIMIT, limit)
            put(EventNew.EVENT_OFFSET, offset)
            put(EventNew.EVENT_SORT_TYPE, "desc")
            put(EventNew.EVENT_SORT_FIELD, "id")
            put(EventNew.EVENT_STATUS, "approved,registration,registrationFinished,running")
            put(EventNew.EVENT_PUBLIC, "true")
            put(
                EventNew.EVENT_BINDS,
                "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState"
            )

            if (searchText.isNotEmpty()) put(EventNew.EVENT_SEARCH, "%$searchText%")
            if (!filter.name.isNullOrEmpty()) put(EventNew.EVENT_NAME, "%" + filter.name + "%")
            if (filter.dateStart != null) put(
                EventNew.EVENT_START_DATE,
                filter.dateStart + "," + filter.dateFinish
            )
            if (filter.format != null) put(EventNew.EVENT_FORMAT, filter.format!!)
            if (!filter.address.isNullOrEmpty() || filter.fullAddress != null) {
                if (filter.fullAddress != null) {
                    if (filter.fullAddress?.country != null) put(
                        EventNew.EVENT_ADDRESS_COUNTRY,
                        filter.fullAddress?.country!!
                    )
                    if (filter.fullAddress?.city != null) put(
                        EventNew.EVENT_ADDRESS_CITY,
                        filter.fullAddress?.city!!
                    )
                    if (filter.fullAddress?.region != null) put(
                        EventNew.EVENT_ADDRESS_REGION,
                        filter.fullAddress?.region!!
                    )
                    if (filter.fullAddress?.street != null) put(
                        EventNew.EVENT_ADDRESS_STREET,
                        filter.fullAddress?.street!!
                    )

                } else {

                }
            }
            val category = filter.spec ?: filter.theme
            if (category != null) put(EventNew.EVENT_CATEGORY, category)
        }
        eventRepository.getEventsList(data)
        */

        val data = buildNewFilters(limit, offset)
        eventRepository.searchEventsNew(data)
    }

    private var isCommonDataLoaded = false
    private var interests: Map<InterestNew, List<InterestNew>>? = null
    private var formats: List<NewEventFormat>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val loadInterests = userRepository.getInterestsList(null)
            .map { interests ->
                interests.data.groupByNotNull { child -> interests.data.firstOrNull { it.id == child.parent } }
            }
        compositeDisposable += Maybe.zip(loadInterests,
            eventRepository.getEventFormatsList(
                mapOf(
                    EventNew.EVENT_LIMIT to 100,
                    EventNew.EVENT_OFFSET to 0
                )
            ),
            BiFunction<Map<InterestNew, List<InterestNew>>, List<NewEventFormat>, Unit> { interests, formats ->
                this.interests = interests
                this.formats = formats
            })
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    isCommonDataLoaded = true
                    onReceiveError(it)
                },
                onSuccess = {
                    isCommonDataLoaded = true
                })
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

    override fun onActionRegister(event: String) = viewState.showEventRequest(event)

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEventDetails(event))
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple {
                pagination.invalidate()
            }
    }


    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onShowFormatClick(format: Int) {
        filter.format = format
        tmpFilter.format = format
        pagination.invalidateFromStart()
    }

    override fun onShowFilterRequest() {
        val showFilter = {
            tmpFilter.interests = this.interests
            tmpFilter.formats = this.formats
            super.onShowFilterRequest()
        }
        if (isCommonDataLoaded) showFilter()
        else {
            compositeDisposable += Completable.complete()
                .timeout(3, TimeUnit.SECONDS)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    showFilter()
                }, {
                    showFilter()
                })
        }
    }

    override fun createFilter() = SearchFilter.EventNew()
    override fun copyFilter(filter: SearchFilter.EventNew) = filter.copy()

    private fun buildNewFilters(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(EventNew.EVENT_LIMIT, limit)
            put(EventNew.EVENT_OFFSET, offset)

            put(SEARCH_EVENT_TYPE, true)
            put(
                SEARCH_EVENT_BINDS,
                "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState"
            )
            if (searchText.isNotEmpty()) put(EventNew.EVENT_SEARCH, "%$searchText%")

            if (!filter.name.isNullOrEmpty()) put(SEARCH_EVENT_NAME, "%" + filter.name + "%")

            if (filter.format != null) put(EventNew.EVENT_FORMAT, filter.format!!)

            if (filter.dateStart != null) put(EventNew.EVENT_START_DATE, filter.dateStart + "," + filter.dateFinish)

            if (!filter.address.isNullOrEmpty() || filter.fullAddress != null) {
                if (filter.fullAddress != null) {
                    if (filter.fullAddress?.country != null) put(
                        EventNew.EVENT_ADDRESS_COUNTRY,
                        filter.fullAddress?.country!!
                    )
                    if (filter.fullAddress?.city != null) put(
                        EventNew.EVENT_ADDRESS_CITY,
                        filter.fullAddress?.city!!
                    )
                    if (filter.fullAddress?.region != null) put(
                        EventNew.EVENT_ADDRESS_REGION,
                        filter.fullAddress?.region!!
                    )
                    if (filter.fullAddress?.street != null) put(
                        EventNew.EVENT_ADDRESS_STREET,
                        filter.fullAddress?.street!!
                    )
                }
            }

//            val interests = filter.spec ?: filter.theme
//            if (interests != null) put(SEARCH_EVENT_INTERESTS, interests)
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
    }
}