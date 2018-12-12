package com.example.ui.mapTabs

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MapTabsPresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<MapTabsContract.View>(), MapTabsContract.Presenter {

    private var selectedPagePosition = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.initPagesWithEvent(appData.event!!)
    }

    override fun attachView(view: MapTabsContract.View?) {
        super.attachView(view)
        viewState.selectPageAtPosition(selectedPagePosition)
    }

    override fun onPageSelected(position: Int) {
        selectedPagePosition = position
    }
}
