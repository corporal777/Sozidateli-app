package com.example.ui.eventsTabs

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventsTabsPresenter
@Inject constructor(
) : BasePresenter<EventsTabsContract.View>(), EventsTabsContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply { selectRecommendedTab() }
    }

    override fun onRecommendedTabClick() = viewState.selectRecommendedTab()

    override fun onSubscriptionsClick() = viewState.selectSubscriptionsTab()

    override fun onEventsClick() = viewState.selectEventsTab()

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuSearchClick() = viewState.showSearch()

    override fun onMenuAccountClick() = viewState.showAccount()
}
