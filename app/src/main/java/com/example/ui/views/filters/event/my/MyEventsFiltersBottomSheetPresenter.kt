package com.example.ui.views.filters.event.my

import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.views.filters.BaseFiltersBottomSheetPresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class MyEventsFiltersBottomSheetPresenter
@Inject constructor(
    val appData: AppData,
    commonRepository: CommonRepository,
    eventRepository: EventRepository
) : BaseFiltersBottomSheetPresenter<MyEventsFiltersBottomSheetContract.View>(commonRepository, eventRepository),
    MyEventsFiltersBottomSheetContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            initTextFilter()
            initAddressFilter()
            initDateFilter()
        }
    }

}