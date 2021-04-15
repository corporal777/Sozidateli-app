package com.example.ui.search.event

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
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
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
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
        private val commonRepository: CommonRepository
) : SearchPresenter<SearchEventContract.View, /*EventNew*/Event, SearchFilter./*EventNew*/Event>(), SearchEventContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        eventRepository.getEventList(limit, offset, buildFilter())
    }
    /*override val pagination = PaginationDataSourceFactory { limit, offset ->
        val data = mutableMapOf<String, Any>().apply {
            put(EventNew.EVENT_LIMIT, limit)
            put(EventNew.EVENT_OFFSET, offset)
            put(EventNew.EVENT_BINDS, "rights,organization,tag,page,activity,user-registration,user-form-result")
            if (searchText.isNotEmpty()) put(EventNew.EVENT_SEARCH, "%$searchText%")
            if (!filter.name.isNullOrEmpty()) put(EventNew.EVENT_NAME, "%"+filter.name+"%")
            if (filter.dateStart != null) put(EventNew.EVENT_START_DATE, "%"+filter.dateStart+"%")
            if (filter.format != null) put(EventNew.EVENT_FORMAT, filter.format!!)
            val category = filter.spec ?: filter.theme
            if (category != null) put(EventNew.EVENT_CATEGORY, category)
        }
        eventRepository.getEventsList(data)
    }*/

    private var isCommonDataLoaded = false
    private var interests: Map</*InterestNew*/Interest, List</*InterestNew*/Interest>>? = null
    private var formats: List</*NewEventFormat*/EventFormat>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val loadInterests = commonRepository.getInterests()
                .map { interests ->
                    interests.groupByNotNull { child -> interests.firstOrNull { it.id == child.parent } }
                }
        compositeDisposable += Maybe.zip(loadInterests, commonRepository.getEventFormats(), BiFunction<Map<Interest, List<Interest>>, List<EventFormat>, Unit> { interests, formats ->
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
        /*val loadInterests = userRepository.getInterestsList(null)
                .map { interests ->
                    interests.data.groupByNotNull { child -> interests.data.firstOrNull { it.id == child.parent } }
                }
        compositeDisposable += Maybe.zip(loadInterests, eventRepository.getEventFormatsList(mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0)),
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
                        })*/
    }

    private fun groupUserInterests(
            userInterests: List<Int>?,
            interests: List<InterestNew>?
    ): Map<InterestNew, MutableList<InterestNew>> {
        val groups = mutableMapOf<InterestNew, MutableList<InterestNew>>()
        val headers = interests?.filter { it.parent == 0 }
        headers?.forEach {
            val parent = interests.filter { parent -> parent.parent == it.id }
            parent.let { it1 ->
                userInterests?.forEach { usIn ->
                    val isUserInterest = it1.find { it2 -> it2.id == usIn }
                    if (isUserInterest != null)
                        groups.getOrPut(it) { mutableListOf() }.add(isUserInterest)
                }
            }
        }
        return groups
    }

    override fun onResume(searchInterface: SearchInterface) {
        super.onResume(searchInterface)
        searchInterface.apply {
            val initWithFilter = this.initWithFilter
            if (initWithFilter != null && initWithFilter is SearchFilter./*EventNew*/Event) {
                tmpFilter = initWithFilter
                filter = initWithFilter
                this.initWithFilter = null
            }
        }
    }

    override fun onActionRegister(event: String) = viewState.showEventRequest(event)

    override fun onActionCancel(event: String) {
        compositeDisposable += eventRepository.eventRegisterCancel(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    pagination.invalidate()
                }
    }

    override fun onActionWriteToOrganization(emails: List</*EventPhoneModel*/EmailAffiliation>) {
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: /*EventPhoneModel*/EmailAffiliation) {
        viewState.showWriteToOrganization(email)
    }

    override fun onActionShowEvent(event: String) {
        compositeDisposable += userRepository.getUserShortNew().ignoreElement().onErrorComplete()
                .andThen(eventData.load(event))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }
        /*compositeDisposable += eventRepository.setDefaultEvent(event)
                .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
                .andThen(eventData.load(event))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }*/
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

    private fun buildFilter(): Map<String, Any> = mutableMapOf<String, Any>().apply {
        put(FILTER_SHOW_CANCELED, true)
        //if (searchText.isNotEmpty()) put(FILTER_CONTENT, searchText)
        val address = filter.address
        if (!address.isNullOrEmpty()) put(FILTER_ADDRESS, address)
        /*val name = filter.name
        if (!name.isNullOrEmpty()) put(FILTER_NAME, name)*/
        val registration = filter.registration
        if (registration != null) put(FILTER_REGISTRATION, registration)
        /*val dateStart = filter.dateStart
        if (dateStart != null) put(FILTER_DATE_START, dateStart)*/
        val dateFinish = filter.dateFinish
        if (dateFinish != null) put(FILTER_DATE_FINISH, dateFinish)
        /*val category = filter.spec ?: filter.theme
        if (category != null) put(FILTER_CATEGORY, category)*/
        /*val format = filter.format
        if (format != null) put(FILTER_FORMAT, format)*/
    }

    override fun createFilter() = SearchFilter./*EventNew*/Event()
    override fun copyFilter(filter: SearchFilter./*EventNew*/Event) = filter.copy()
}