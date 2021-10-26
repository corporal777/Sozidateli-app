package com.example.ui.event.location

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.MapInfo
import com.example.data.models.Place
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventLocationPresenter
@Inject constructor(appData: AppData) : BasePresenter<EventLocationContract.View>(appData), EventLocationContract.Presenter {

    var mapInfo: MapInfo? = null
    var places: Array<Place>? = null

    private var selectedPagePosition = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val mapInfo = mapInfo
        val places = places
        if (mapInfo == null && places == null) throw NullPointerException("One of mapInfo or places must not be null")
        viewState.initPages(mapInfo, places)
    }

    override fun attachView(view: EventLocationContract.View?) {
        super.attachView(view)
        viewState.selectPageAtPosition(selectedPagePosition)
    }

    override fun onPageSelected(position: Int) {
        selectedPagePosition = position
    }
}
