package com.example.ui.eventsTabs

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventsTabsPresenter
@Inject constructor(
) : BasePresenter<EventsTabsContract.View>(), EventsTabsContract.Presenter {

    override fun onRecommendedTabClick() = viewState.selectRecommendedTab()

    override fun onSubscriptionsClick() = viewState.selectSubscriptionsTab()

    override fun onEventsClick() = viewState.selectEventsTab()
}
