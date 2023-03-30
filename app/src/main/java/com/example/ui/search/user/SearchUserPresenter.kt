package com.example.ui.search.user

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.InterestNew
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_AREA
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_CITY
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_COUNTRY
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_FEDERAL
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_FLAT
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_HOUSE
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_INDEX
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_REGION
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_SETTLEMENT
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_STREET
import com.example.data.models.UserDetail.Companion.USER_BINDS
import com.example.data.models.UserDetail.Companion.USER_LIMIT
import com.example.data.models.UserDetail.Companion.USER_OFFSET
import com.example.data.models.UserDetail.Companion.USER_SEARCH
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.search.SearchInterface
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
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
        /*
        val data = mutableMapOf<String, Any>().apply {
             put(USER_LIMIT, limit)
             put(USER_OFFSET, offset)
             put(USER_BINDS, "userFavorite")
 //            val address = filter.address
 //            if (!address.isNullOrEmpty()) put(USER_ADDRESS_STREET, address)

             if (searchText.isNotEmpty()) put(USER_SEARCH, searchText.trim())
             val interest = filter.spec ?: filter.theme
             if (interest != null) put(FILTER_INTEREST, interest)

             //new address filters
             val index = filter.index
             if (!index.isNullOrEmpty()) put(USER_ADDRESS_INDEX, index)
             val country = filter.country
             if (!country.isNullOrEmpty()) put(USER_ADDRESS_COUNTRY, country)
             val federal = filter.federal
             if (!federal.isNullOrEmpty()) put(USER_ADDRESS_FEDERAL, federal)
             val region = filter.region
             if (!region.isNullOrEmpty()) put(USER_ADDRESS_REGION, region)
             val area = filter.area
             if (!area.isNullOrEmpty()) put(USER_ADDRESS_AREA, area)
             val city = filter.city
             if (!city.isNullOrEmpty()) put(USER_ADDRESS_CITY, city)
             val settlement = filter.settlement
             if (!settlement.isNullOrEmpty()) put(USER_ADDRESS_SETTLEMENT, settlement)
             val street = filter.street
             if (!street.isNullOrEmpty()) put(USER_ADDRESS_STREET, street)
             val house = filter.house
             if (!house.isNullOrEmpty()) put(USER_ADDRESS_HOUSE, house)
             val flat = filter.flat
             if (!flat.isNullOrEmpty()) put(USER_ADDRESS_FLAT, flat)

         }
         userRepository.getUsers(data).doOnSuccess {
             val uid = appData.getId()
             it.data.forEach { user -> user?.isCurrentUser = user?.id == uid }
         }
         */


        val data = buildFilterNew(limit, offset)
        userRepository.searchUsersNew(data).doOnSuccess {
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
        if (isSubscribed) {
            compositeDisposable += eventRepository.deleteFromFavorite(user.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    user.binds?.userFavorite = null
                    viewState.updateUser(user)
                }
        } else {
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, user.id)
                )
            )
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    user.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.updateUser(user)
                }
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
                .withCustomProgressBarLoadingDialog(viewState)
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


    private fun buildFilterNew(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(USER_LIMIT, limit)
            put(USER_OFFSET, offset)

            if (searchText.isNotEmpty()) put(USER_SEARCH, searchText.trim())

            val interest = filter.spec ?: filter.theme
            if (interest != null) put(FILTER_INTEREST, interest)

            //new age filter
            if (filter.ageFrom != null) put(SEARCH_AGE_FROM, filter.ageFrom!!)
            if (filter.ageTo != null) put(SEARCH_AGE_TO, filter.ageTo!!)

            //new binds filter
            put(SEARCH_USER_BINDS, "userFavorite")
            put(SEARCH_USER_TYPE, true)

            //new address filters
            val index = filter.index
            if (!index.isNullOrEmpty()) put(USER_ADDRESS_INDEX, index)
            val country = filter.country
            if (!country.isNullOrEmpty()) put(USER_ADDRESS_COUNTRY, country)
            val federal = filter.federal
            if (!federal.isNullOrEmpty()) put(USER_ADDRESS_FEDERAL, federal)
            val region = filter.region
            if (!region.isNullOrEmpty()) put(USER_ADDRESS_REGION, region)
            val area = filter.area
            if (!area.isNullOrEmpty()) put(USER_ADDRESS_AREA, area)
            val city = filter.city
            if (!city.isNullOrEmpty()) put(USER_ADDRESS_CITY, city)
            val settlement = filter.settlement
            if (!settlement.isNullOrEmpty()) put(USER_ADDRESS_SETTLEMENT, settlement)
            val street = filter.street
            if (!street.isNullOrEmpty()) put(USER_ADDRESS_STREET, street)
            val house = filter.house
            if (!house.isNullOrEmpty()) put(USER_ADDRESS_HOUSE, house)
            val flat = filter.flat
            if (!flat.isNullOrEmpty()) put(USER_ADDRESS_FLAT, flat)

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

        private const val SEARCH_USER_TYPE = "user"
        private const val SEARCH_USER_BINDS = "userBinds"
    }
}