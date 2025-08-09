package com.example.ui.views.filters.event

//@InjectViewState
//class EventFiltersBottomSheetPresenter
//@Inject constructor(
//    val appData: AppData,
//    val organizationRepository: OrganizationRepository,
//    commonRepository: CommonRepository,
//    eventRepository: EventRepository
//) : BaseFiltersBottomSheetPresenter<EventFiltersBottomSheetContract.View>(commonRepository, eventRepository),
//    EventFiltersBottomSheetContract.Presenter {
//
//    override fun onFirstViewAttach() {
//        super.onFirstViewAttach()
//        viewState.apply {
//            initTextFilter()
//            initRegions()
//            initTowns()
//            initDateStart()
//            initDateEnd()
//        }
//        compositeDisposable += organizationRepository.getOrganizationsWithActiveEvents()
//            .performOnBackgroundOutOnMain()
//            .subscribeBy {
//                viewState.initOrganizations(it)
//            }
//    }
//
//    fun getFormatName(
//        formats: List<NewEventFormat>,
//        filterFormat: Int?,
//        customFormat: String?
//    ): String? {
//        return if (filterFormat != null) formats.find { it.id == filterFormat }?.name
//        else if (!customFormat.isNullOrBlank()) customFormat
//        else null
//    }
//
//    fun getOrgName(
//        organizations: List<OrganizationNew>?,
//        organizationName: String?,
//        organizationId: Long?
//    ): String? {
//        if (organizationId != null) {
//            return organizations?.find { it.id == organizationId }?.legalInformation?.name?.short
//        } else if (!organizationName.isNullOrBlank()) {
//            return organizationName
//        } else return null
//    }
//
//}