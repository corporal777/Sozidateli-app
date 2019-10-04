package com.example.ui.search

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.DataArgsSearchType
import com.example.data.models.Event
import com.example.data.models.SearchTypeEvent
import com.example.events.OnAddSearchTypeEvent
import com.example.extensions.build
import com.example.holders.SearchEventResultItem
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.TYPE_DATE
import com.example.util.TYPE_DATE_PERIOD_FROM
import com.example.util.TYPE_DATE_PERIOD_TO
import com.example.util.Utils
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SearchPresenter
@Inject constructor(private val eventRepository: EventRepository
) : BasePresenter<SearchContract.View>(), SearchContract.Presenter {

    private var searchHolder: SearchHolder = SearchHolder()
    private var typeTextCompositeDisposable = CompositeDisposable()

    val factory = PaginationDataSourceFactory { limit, offset ->
        eventRepository.getEventList(limit, offset, searchHolder.text,
                dateLongToStrinng(searchHolder.dateFrom),
                dateLongToStrinng(searchHolder.dateTo), searchHolder.categories.map { it.id }, searchHolder.organizations.map { it.id })
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setSearchData(searchHolder)
        EventBus.getDefault().register(this)
    }

    private fun dateLongToStrinng(date: Long): String? {
        if (date == 0L) return null
        return Utils.defaultServerDateFormatter.format(date)
    }

    override fun attachView(view: SearchContract.View?) {
        super.attachView(view)
        viewState.updateCategoryList(searchHolder)
        viewState.updateOrganizationList(searchHolder)
    }

    override fun onSearchTextChange(text: String) {
        typeTextCompositeDisposable.clear()
        searchHolder.text = text
        Observable.timer(400, TimeUnit.MILLISECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    search()
                }, {}).call(typeTextCompositeDisposable)

    }

    override fun removeOrganizationItem(searchTypeEvent: SearchTypeEvent) {
        searchHolder.organizations.remove(searchTypeEvent)
        viewState.updateOrganizationList(searchHolder)
        search()
    }

    override fun removeCategoryItem(searchTypeEvent: SearchTypeEvent) {
        searchHolder.categories.remove(searchTypeEvent)
        viewState.updateCategoryList(searchHolder)
        search()
    }

    override fun onOrganizationClick() {
        val data = DataArgsSearchType(arrayListOf(), true)
        data.array.addAll(searchHolder.organizations)
        viewState.showPlaces(data)
    }

    override fun onCategoryClick() {
        val data = DataArgsSearchType(arrayListOf(), false)
        data.array.addAll(searchHolder.categories)
        viewState.showTypeEvent(data)
    }

    private fun search() {
        searchHolder.totalCountSearchResult = 0
        factory.mapIndexedTotal { item, index, total ->
            searchHolder.totalCountSearchResult = total
            return@mapIndexedTotal SearchEventResultItem(item, this@SearchPresenter)
        }
                .build()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showSearchResult(it, searchHolder.totalCountSearchResult)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(event: OnAddSearchTypeEvent) {
        if (event.isOrganization) {
            searchHolder.organizations.clear()
            searchHolder.organizations.addAll(event.data)
        } else {
            searchHolder.categories.clear()
            searchHolder.categories.addAll(event.data)
        }
        EventBus.getDefault().removeStickyEvent(event)
        search()
    }

    override fun onClickDate(type: String) {
        var date = 0L
        when (type) {
            TYPE_DATE -> {
                date = searchHolder.date
            }
            TYPE_DATE_PERIOD_FROM -> {
                date = searchHolder.dateFrom
            }
            TYPE_DATE_PERIOD_TO -> {
                date = searchHolder.dateTo
            }
        }
        viewState.showDateDialog(date, type)
    }

    override fun onDateSelected(date: Long, type: String) {
        when (type) {
            TYPE_DATE -> {
                searchHolder.date = date
            }
            TYPE_DATE_PERIOD_FROM -> {
                searchHolder.dateFrom = date
            }
            TYPE_DATE_PERIOD_TO -> {
                searchHolder.dateTo = date
            }
        }
        viewState.setSearchData(searchHolder)
        search()
    }

    override fun clearFilter() {
        searchHolder.clear()
        viewState.setSearchData(searchHolder)
        viewState.updateCategoryList(searchHolder)
        viewState.updateOrganizationList(searchHolder)
        viewState.hideSearchResultLabel()
    }

    override fun onEventClick(event: Event) {
        viewState.showEvent(event)
    }

    override fun onGoToEventClick(event: Event) = viewState.showEventRequest(event)

    override fun onQrScanClick() = viewState.showQrScan()

    override fun onDestroy() {
        super.onDestroy()
        typeTextCompositeDisposable.clear()
        EventBus.getDefault().unregister(this)
    }
}
