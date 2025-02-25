package com.example.ui.search.chat

import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.extensions.buildFlow
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withTimeOut
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class SearchChatPresenter
@Inject constructor(
    val appData: AppData,
    val userRepository: UserRepository,
    val eventRepository: EventRepository,
    val commonRepository: CommonRepository
) : BasePresenter<SearchChatContract.View>(appData), SearchChatContract.Presenter {

    private var filter = SearchFilter.UserNew()
    private var searchText = ""

    private val pagination = PagingDataSourceFactory { limit, offset ->
        userRepository.getUsers(buildFilters(limit, offset))
            .doOnSuccess { it.data.forEach { user -> user.setIfCurrentUser(appData.getId()) } }
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 30, distance = 5)



    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
    }

    override fun attachView(view: SearchChatContract.View?) {
        super.attachView(view)
        viewState.setFiltersChosen(filter.isHasFilter())
    }

    override fun onUserClick(user: UserDetail) {
        if (appData.isCurrentUser(user.id.toString())) viewState.showCurrentUser()
        else viewState.showUser(user)
    }

    override fun onUserActionCLick(user: UserDetail) {
        compositeDisposable += userRepository.addOrRemoveUserFavorite(user)
            .doOnSuccess { user.binds?.userFavorite = it.value }
            .withTimeOut(5000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.updateUser(user)
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.updateUser(user)
                    if (it.value == null) viewState.showRemovedFromFavoriteDialog()
                    else viewState.showAddedToFavoriteDialog()
                })
    }

    override fun onRefreshRequest() = pagination.invalidate()

    override fun onFilterClick() = viewState.showFilter(filter)

    override fun onFilterApplyClick(newFilter: SearchFilter.UserNew) {
        filter = newFilter
        viewState.setFiltersChosen(filter.isHasFilter())
        pagination.invalidate()
    }

    override fun onSearchTextChange(text: String) {
        if (searchText == text) return
        searchText = text
        pagination.invalidate()
    }

    override fun onChangeOffset(offset: Int) = viewState.changeAppBarElevation(abs(offset / 10f))

    private fun buildFilters(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(UserDetail.USER_LIMIT, limit)
            put(UserDetail.USER_OFFSET, offset)
            put(UserDetail.USER_BINDS, "userFavorite")

            if (searchText.isNotEmpty()) put(UserDetail.USER_SEARCH, searchText.trim())

            val interest = filter.spec ?: filter.theme
            if (interest != null) put(FILTER_INTEREST, interest)

            //new address filters
            val index = filter.index
            if (!index.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_INDEX, index)
            val country = filter.country
            if (!country.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_COUNTRY, country)
            val federal = filter.federal
            if (!federal.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_FEDERAL, federal)
            val region = filter.region
            if (!region.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_REGION, region)
            val area = filter.area
            if (!area.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_AREA, area)
            val city = filter.city
            if (!city.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_CITY, city)
            val settlement = filter.settlement
            if (!settlement.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_SETTLEMENT, settlement)
            val street = filter.street
            if (!street.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_STREET, street)
            val house = filter.house
            if (!house.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_HOUSE, house)
            val flat = filter.flat
            if (!flat.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_FLAT, flat)


        }
    }

    companion object {
        private const val FILTER_INTEREST = "interests"
    }
}