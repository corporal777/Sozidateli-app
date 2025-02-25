package com.example.ui.views.filters.organization

import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.views.filters.BaseFiltersBottomSheetPresenter
import com.example.ui.views.filters.user.UserFiltersBottomSheetContract
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class OrgFiltersBottomSheetPresenter
@Inject constructor(
    val appData: AppData,
    commonRepository: CommonRepository,
    eventRepository: EventRepository
) : BaseFiltersBottomSheetPresenter<OrgFiltersBottomSheetContract.View>(commonRepository, eventRepository),
    OrgFiltersBottomSheetContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            initOrganizationName()
            initOrganizationInn()
            initRegions()
            initTowns()
        }
    }
}