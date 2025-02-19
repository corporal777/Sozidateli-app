package com.example.ui.search.user

import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_CITY
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_REGION
import com.example.data.models.UserDetail.Companion.USER_LIMIT
import com.example.data.models.UserDetail.Companion.USER_OFFSET
import com.example.data.models.UserDetail.Companion.USER_SEARCH
import com.example.extensions.buildFlow
import com.example.repository.UserRepository
import com.example.ui.search.SearchPresenter
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withTimeOut
import javax.inject.Inject

@InjectViewState
class SearchUserPresenter
@Inject constructor(
    val appData: AppData,
    val userRepository: UserRepository
) : SearchPresenter<SearchUserContract.View>(appData), SearchUserContract.Presenter {

    private var userFilter = SearchFilter.UserNew()

    private val pagination = PagingDataSourceFactory { limit, offset ->
        userRepository.searchUsers(buildFilterNew(limit, offset))
            .doOnSuccess { it.data.forEach { user -> user.setIfCurrentUser(appData.getId()) } }
    }.applyErrorHandler { onReceivePagingError(it) }
        .buildFlow(initialSize = SEARCH_PAGE_SIZE, distance = 5)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
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


    override fun onFiltersApplyClick(filter: SearchFilter.UserNew) {
        userFilter = filter
        viewState.setHasFilter()
        pagination.invalidate()
    }

    override fun onRefreshRequest() = pagination.invalidate()

    override fun onShowFilterRequest() = viewState.showFilter(userFilter)

    override fun isHasFilter(): Boolean = userFilter.isHasFilter()

    private fun buildFilterNew(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(USER_LIMIT, limit)
            put(USER_OFFSET, offset)

            //new binds filter
            put(SEARCH_USER_BINDS, "userFavorite")
            put(SEARCH_USER_TYPE, true)

            if (searchText.isNotEmpty()) put(USER_SEARCH, searchText.trim())

            //new interests filter
            if (userFilter.theme != null) put(SEARCH_THEME, userFilter.theme!!)
            if (userFilter.spec != null) put(SEARCH_SPEC, userFilter.spec!!)

            //new age filter
            if (userFilter.ageFrom != null) put(SEARCH_AGE_FROM, userFilter.ageFrom!!)
            if (userFilter.ageTo != null) put(SEARCH_AGE_TO, userFilter.ageTo!!)

            //new address filters
            if (!userFilter.addressRegion.isNullOrEmpty())
                put(USER_ADDRESS_REGION, userFilter.addressRegion!!)
            if (!userFilter.addressTown.isNullOrEmpty())
                put(USER_ADDRESS_CITY, userFilter.addressTown!!)
            if (!userFilter.addressTownType.isNullOrEmpty())
                put("type", userFilter.addressTownType!!)
        }
    }


    companion object {
        private const val SEARCH_PAGE_SIZE = 30

        private const val SEARCH_AGE_FROM = "ageFrom"
        private const val SEARCH_AGE_TO = "ageTo"

        private const val SEARCH_THEME = "themes"
        private const val SEARCH_SPEC = "specialization"

        private const val SEARCH_USER_TYPE = "user"
        private const val SEARCH_USER_BINDS = "userBinds"
    }
}