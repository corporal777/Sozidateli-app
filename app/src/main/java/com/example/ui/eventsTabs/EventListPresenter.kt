package com.example.ui.eventsTabs

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventListPresenter
@Inject constructor(
) : BasePresenter<EventListContract.View>(), EventListContract.Presenter {

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuSearchClick() = viewState.showSearch()

    override fun onMenuAccountClick() = viewState.showAccount()
}
