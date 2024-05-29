package com.example.ui.search.user

import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_CITY
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_REGION
import com.example.data.models.UserDetail.Companion.USER_LIMIT
import com.example.data.models.UserDetail.Companion.USER_OFFSET
import com.example.data.models.UserDetail.Companion.USER_SEARCH
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SearchUserPresenter
@Inject constructor(
    val appData: AppData,
    val userRepository: UserRepository,
    private val commonRepository: CommonRepository,
    val eventRepository: EventRepository
) : SearchPresenter<SearchUserContract.View, UserDetail, SearchFilter.UserNew>(appData),
    SearchUserContract.Presenter {


    private var isInterestsLoaded = false
    private var interests: Map<InterestNew, List<InterestNew>>? = null


    override val pagination = PaginationDataSourceFactory { limit, offset ->
        val data = buildFilterNew(limit, offset)
        userRepository.searchUsers(data).doOnSuccess {
            val uid = appData.getId()
            it.data.forEach { user -> user?.isCurrentUser = user?.id == uid }
        }
    }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += commonRepository.getInterests()
            .map { interests ->
                interests.groupByNotNull { child -> interests.firstOrNull { it.id == child.parent } }
            }
            .performOnBackgroundOutOnMain()
            .subscribe({
                isInterestsLoaded = true
                this.interests = it
            }, {
                it.printStackTrace()
                isInterestsLoaded = true
            })
    }


    override fun onUserClick(user: UserDetail) {
        if (appData.isCurrentUser(user.id.toString())) viewState.showCurrentUser()
        else viewState.showUser(user)
    }

    override fun onUserActionCLick(user: UserDetail) {
        val isSubscribed = user.binds?.userFavorite != null
        compositeDisposable += Completable.defer {
            if (isSubscribed)
                eventRepository.deleteFromFavorites(user.binds?.userFavorite?.id.toString())
                    .doOnComplete { user.binds?.userFavorite = null }
            else eventRepository.addUserToFavorites(user.id.toString())
                .doOnSuccess { user.binds?.userFavorite = EventUserFavorite(it.id, it.user) }
                .ignoreElement()
        }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.updateUser(user)
                if (user.binds?.userFavorite == null) viewState.showRemovedFromFavoriteDialog()
                else viewState.showAddedToFavoriteDialog()
            }
    }

    override fun onShowFilterRequest() {
        val showFilter = {
            tmpFilter.interests = this.interests
            super.onShowFilterRequest()
        }
        if (isInterestsLoaded) showFilter()
        else {
            compositeDisposable += Completable.complete()
                .timeout(3, TimeUnit.SECONDS)
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribe({
                    showFilter()
                }, {
                    showFilter()
                })
        }
    }

    override fun createFilter() = SearchFilter.UserNew()
    override fun copyFilter(filter: SearchFilter.UserNew) = filter.copy()
    override fun isHasFilter(): Boolean = filter.isHasFilter()
    override fun getSearchType(): String = SEARCH_USER_TYPE


    private fun buildFilterNew(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(USER_LIMIT, limit)
            put(USER_OFFSET, offset)

            if (searchText.isNotEmpty()) put(USER_SEARCH, searchText.trim())

            //new interests filter
            if (filter.theme != null) put(SEARCH_THEME, filter.theme!!)
            if (filter.spec != null) put(SEARCH_SPEC, filter.spec!!)

            //new age filter
            if (filter.ageFrom != null) put(SEARCH_AGE_FROM, filter.ageFrom!!)
            if (filter.ageTo != null) put(SEARCH_AGE_TO, filter.ageTo!!)

            //new binds filter
            put(SEARCH_USER_BINDS, "userFavorite")
            put(SEARCH_USER_TYPE, true)

            //new address filters
            if (!filter.addressRegion.isNullOrEmpty()) {
                put(USER_ADDRESS_REGION, filter.addressRegion!!)
            }
            if (!filter.addressTown.isNullOrEmpty()) {
                put(USER_ADDRESS_CITY, filter.addressTown!!)
            }
            if (!filter.addressTownType.isNullOrEmpty()) {
                put("type", filter.addressTownType!!)
            }


//            val index = filter.index
//            if (!index.isNullOrEmpty()) put(USER_ADDRESS_INDEX, index)
//            val country = filter.country
//            if (!country.isNullOrEmpty()) put(USER_ADDRESS_COUNTRY, country)
//            val federal = filter.federal
//            if (!federal.isNullOrEmpty()) put(USER_ADDRESS_FEDERAL, federal)
//            val region = filter.region
//            if (!region.isNullOrEmpty()) put(USER_ADDRESS_REGION, region)
//            val area = filter.area
//            if (!area.isNullOrEmpty()) put(USER_ADDRESS_AREA, area)
//            val city = filter.city
//            if (!city.isNullOrEmpty()) put(USER_ADDRESS_CITY, city)
//            val settlement = filter.settlement
//            if (!settlement.isNullOrEmpty()) put(USER_ADDRESS_SETTLEMENT, settlement)
//            val street = filter.street
//            if (!street.isNullOrEmpty()) put(USER_ADDRESS_STREET, street)
//            val house = filter.house
//            if (!house.isNullOrEmpty()) put(USER_ADDRESS_HOUSE, house)
//            val flat = filter.flat
//            if (!flat.isNullOrEmpty()) put(USER_ADDRESS_FLAT, flat)

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

    companion object {
        private const val FILTER_NAME = "user_fio"
        private const val FILTER_ADDRESS = "user_address"
        private const val FILTER_EMAIL = "user_email"
        private const val FILTER_PHONE = "user_phone"
        private const val FILTER_FAVORITES = "is_in_favorite"
        private const val FILTER_INTEREST = "interests"
        private const val FILTER_AGE = "user_age"

        private const val SEARCH_AGE_FROM = "ageFrom"
        private const val SEARCH_AGE_TO = "ageTo"

        private const val SEARCH_THEME = "themes"
        private const val SEARCH_SPEC = "specialization"

        private const val SEARCH_USER_TYPE = "user"
        private const val SEARCH_USER_BINDS = "userBinds"
    }
}