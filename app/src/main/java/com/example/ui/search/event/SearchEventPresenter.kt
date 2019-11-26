package com.example.ui.search.event

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.Event.Companion.FILTER_ADDRESS
import com.example.data.models.Event.Companion.FILTER_CATEGORY
import com.example.data.models.Event.Companion.FILTER_CONTENT
import com.example.data.models.Event.Companion.FILTER_DATE_FINISH
import com.example.data.models.Event.Companion.FILTER_DATE_START
import com.example.data.models.Event.Companion.FILTER_FORMAT
import com.example.data.models.Event.Companion.FILTER_NAME
import com.example.data.models.Event.Companion.FILTER_REGISTRATION
import com.example.data.models.EventFormat
import com.example.data.models.Interest
import com.example.data.models.SearchFilter
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SearchEventPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val commonRepository: CommonRepository
) : SearchPresenter<SearchEventContract.View, Event, SearchFilter.Event>(), SearchEventContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        eventRepository.getEventList(limit, offset, buildFilter())
    }

    private var isCommonDataLoaded = false
    private var interests: Map<Interest, List<Interest>>? = null
    private var formats: List<EventFormat>? = null

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
                            it.printStackTrace()
                            isCommonDataLoaded = true
                        },
                        onSuccess = {
                            isCommonDataLoaded = true
                        })
    }

    override fun onEventClick(event: Event) {
        viewState.showAboutEvent(event)
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
        if (searchText.isNotEmpty()) put(FILTER_CONTENT, searchText)
        val address = filter.address
        if (!address.isNullOrEmpty()) put(FILTER_ADDRESS, address)
        val name = filter.name
        if (!name.isNullOrEmpty()) put(FILTER_NAME, name)
        val registration = filter.registration
        if (registration != null) put(FILTER_REGISTRATION, registration)
        val dateStart = filter.dateStart
        if (dateStart != null) put(FILTER_DATE_START, dateStart)
        val dateFinish = filter.dateFinish
        if (dateFinish != null) put(FILTER_DATE_FINISH, dateFinish)
        val category = filter.spec ?: filter.theme
        if (category != null) put(FILTER_CATEGORY, category)
        val format = filter.format
        if (format != null) put(FILTER_FORMAT, format)
    }

    override fun createFilter() = SearchFilter.Event()
    override fun copyFilter(filter: SearchFilter.Event) = filter.copy()
}