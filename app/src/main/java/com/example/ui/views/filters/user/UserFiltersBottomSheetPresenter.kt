package com.example.ui.views.filters.user

import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.views.filters.BaseFiltersBottomSheetPresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class UserFiltersBottomSheetPresenter
@Inject constructor(
    val appData: AppData,
    commonRepository: CommonRepository,
    eventRepository: EventRepository
) : BaseFiltersBottomSheetPresenter<UserFiltersBottomSheetContract.View>(commonRepository, eventRepository),
    UserFiltersBottomSheetContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            initRegions()
            initTowns()
            initAgeFrom()
            initAgeTo()
        }
    }

    fun getAgesList(ageFrom: Int?): List<String> {
        return arrayListOf<String>().apply {
            if (ageFrom == null) {
                for (i in 14 until 81) add(i.toString())
            } else {
                for (i in ageFrom until 81) add(i.toString())
            }
        }
    }

}