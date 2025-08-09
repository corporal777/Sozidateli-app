package com.example.ui.views.filters.user

//@InjectViewState
//class UserFiltersBottomSheetPresenter
//@Inject constructor(
//    val appData: AppData,
//    commonRepository: CommonRepository,
//    eventRepository: EventRepository
//) : BaseFiltersBottomSheetPresenter<UserFiltersBottomSheetContract.View>(commonRepository, eventRepository),
//    UserFiltersBottomSheetContract.Presenter {
//
//    override fun onFirstViewAttach() {
//        super.onFirstViewAttach()
//        viewState.apply {
//            initRegions()
//            initTowns()
//            initAgeFrom()
//            initAgeTo()
//        }
//    }
//
//    fun getAgesList(ageFrom: Int?): List<String> {
//        return arrayListOf<String>().apply {
//            if (ageFrom == null) {
//                for (i in 14 until 81) add(i.toString())
//            } else {
//                for (i in ageFrom until 81) add(i.toString())
//            }
//        }
//    }
//
//}