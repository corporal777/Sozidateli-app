package com.example.ui.event.location

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.MapInfo
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventLocationPresenter
@Inject constructor() : BasePresenter<EventLocationContract.View>(), EventLocationContract.Presenter {

    var mapInfo: MapInfo? = null

    private var selectedPagePosition = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val mapInfo = mapInfo
        if (mapInfo != null) viewState.initPages(mapInfo)
    }

    override fun attachView(view: EventLocationContract.View?) {
        super.attachView(view)
        viewState.selectPageAtPosition(selectedPagePosition)
    }

    override fun onPageSelected(position: Int) {
        selectedPagePosition = position
    }
}
