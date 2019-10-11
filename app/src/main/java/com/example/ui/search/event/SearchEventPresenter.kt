package com.example.ui.search.event

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.Interest
import com.example.data.models.SearchFilter
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Completable
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

    private var isInterestsLoaded = false
    private var interests: Map<Interest, List<Interest>>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += commonRepository.getInterests()
                .map { groupInterests(it) }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    isInterestsLoaded = true
                    this.interests = it
                }, {
                    it.printStackTrace()
                    isInterestsLoaded = true
                })
    }

    override fun onEventClick(event: Event) {
        viewState.showAboutEvent(event)
    }

    override fun onShowFilterRequest() {
        val showFilter = {
            tmpFilter.interests = this.interests
            super.onShowFilterRequest()
        }
        if (isInterestsLoaded) showFilter()
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

    private fun groupInterests(interests: List<Interest>): Map<Interest, List<Interest>> {
        val group = linkedMapOf<Interest, MutableList<Interest>>()
        interests.forEach { child ->
            val key = interests.firstOrNull { it.id == child.parent }
            if (key != null) {
                group.getOrPut(key) { mutableListOf() }.apply {
                    add(child)
                }
            }
        }

        return group
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
        val category = filter.theme ?: filter.specialization
        if (category != null) put(FILTER_CATEGORY, category)
    }

    override fun createFilter() = SearchFilter.Event()
    override fun copyFilter(filter: SearchFilter.Event) = filter.copy()

    companion object {
        private const val FILTER_CONTENT = "content"
        private const val FILTER_ADDRESS = "address"
        private const val FILTER_NAME = "name"
        private const val FILTER_REGISTRATION = "is_registered"
        private const val FILTER_DATE_START = "date_start"
        private const val FILTER_DATE_FINISH = "date_end"
        private const val FILTER_CATEGORY = "category"

        const val FILTER_REGISTRATION_PENDING = "pending"
        const val FILTER_REGISTRATION_APPROVED = "approved"
        const val FILTER_REGISTRATION_DECLINED = "declined"
        const val FILTER_REGISTRATION_NOT_REGISTERED = "not_registered"
    }
}