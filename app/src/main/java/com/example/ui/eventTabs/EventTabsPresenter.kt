package com.example.ui.eventTabs

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventTabsPresenter
@Inject constructor(
) : BasePresenter<EventTabsContract.View>(), EventTabsContract.Presenter {

}
