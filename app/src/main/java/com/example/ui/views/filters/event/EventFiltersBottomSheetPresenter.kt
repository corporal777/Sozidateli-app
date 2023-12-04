package com.example.ui.views.filters.event

import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.InterestNew
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.search.SearchPresenter
import com.example.ui.search.user.SearchUserContract
import com.example.ui.views.filters.BaseFiltersBottomSheetPresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class EventFiltersBottomSheetPresenter
@Inject constructor(
    val appData: AppData,
    commonRepository: CommonRepository,
    eventRepository: EventRepository
) : BaseFiltersBottomSheetPresenter<EventFiltersBottomSheetContract.View>(commonRepository, eventRepository),
    EventFiltersBottomSheetContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            initTextFilter()
            initAddressFilter()
            initDateFilter()
        }
    }

}