package com.example.ui.views.filters.event

import com.example.data.AppData
import com.example.data.models.NewEventFormat
import com.example.data.models.OrganizationNew
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.views.filters.BaseFiltersBottomSheetPresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EventFiltersBottomSheetPresenter
@Inject constructor(
    val appData: AppData,
    val organizationRepository: OrganizationRepository,
    commonRepository: CommonRepository,
    eventRepository: EventRepository
) : BaseFiltersBottomSheetPresenter<EventFiltersBottomSheetContract.View>(commonRepository, eventRepository),
    EventFiltersBottomSheetContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            initTextFilter()
            initRegions()
            initTowns()
            initDateStart()
            initDateEnd()
        }
        compositeDisposable += organizationRepository.getOrganizationsWithActiveEvents()
            .performOnBackgroundOutOnMain()
            .subscribeBy {
                viewState.initOrganizations(it)
            }
    }

    fun getFormatName(
        formats: List<NewEventFormat>,
        filterFormat: Int?,
        customFormat: String?
    ): String? {
        return if (filterFormat != null) formats.find { it.id == filterFormat }?.name
        else if (!customFormat.isNullOrBlank()) customFormat
        else null
    }

    fun getOrgName(
        organizations: List<OrganizationNew>?,
        organizationName: String?,
        organizationId: Long?
    ): String? {
        if (organizationId != null) {
            return organizations?.find { it.id == organizationId }?.legalInformation?.name?.short
        } else if (!organizationName.isNullOrBlank()) {
            return organizationName
        } else return null
    }

}