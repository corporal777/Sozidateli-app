package com.example.ui.search

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.DataArgsSearchType
import com.example.data.models.SearchTypeEvent
import com.example.events.OnAddSearchTypeEvent
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.TYPE_DATE
import com.example.util.TYPE_DATE_PERIOD_FROM
import com.example.util.TYPE_DATE_PERIOD_TO
import kotlinx.android.synthetic.main.fragment_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@InjectViewState
class SearchPresenter
@Inject constructor(private val chatRepository: ChatRepository
) : BasePresenter<SearchContract.View>(), SearchContract.Presenter {

    private var searchHolder: SearchHolder = SearchHolder()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)
    }

    override fun attachView(view: SearchContract.View?) {
        super.attachView(view)
        viewState.setSearchData(searchHolder)
        viewState.updateTypeEventsList(searchHolder)
        viewState.updatePlacesList(searchHolder)
    }


    override fun removePlacesItem(searchTypeEvent: SearchTypeEvent) {
        searchHolder.places.remove(searchTypeEvent)
        viewState.updatePlacesList(searchHolder)
    }

    override fun removeTypeEventItem(searchTypeEvent: SearchTypeEvent) {
        searchHolder.typeEvents.remove(searchTypeEvent)
        viewState.updateTypeEventsList(searchHolder)
    }

    override fun onPlacesClick() {
        val data = DataArgsSearchType(arrayListOf(), true)
        data.array.addAll(searchHolder.places)
        viewState.showPlaces(data)
    }

    override fun onTypeEventsClick() {
        val data = DataArgsSearchType(arrayListOf(), false)
        data.array.addAll(searchHolder.typeEvents)
        viewState.showTypeEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(event: OnAddSearchTypeEvent) {
        if (event.isPlaces) {
            searchHolder.places.clear()
            searchHolder.places.addAll(event.data)
        } else {
            searchHolder.typeEvents.clear()
            searchHolder.typeEvents.addAll(event.data)
        }
        EventBus.getDefault().removeStickyEvent(event)
    }

    override fun onClickDate(type: String) {
        var date = 0L
        when(type){
            TYPE_DATE ->{
                date = searchHolder.date
            }
            TYPE_DATE_PERIOD_FROM ->{
                date = searchHolder.dateFrom
            }
            TYPE_DATE_PERIOD_TO ->{
                date = searchHolder.dateTo
            }
        }
        viewState.showDateDialog(date,type)
    }

    override fun onDateSelected(date: Long, type: String) {
        when(type){
            TYPE_DATE->{
                searchHolder.date = date
            }
            TYPE_DATE_PERIOD_FROM->{
                searchHolder.dateFrom = date
            }
            TYPE_DATE_PERIOD_TO->{
                searchHolder.dateTo = date
            }
        }
        viewState.setSearchData(searchHolder)
    }

    override fun onSearchClick() {

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
