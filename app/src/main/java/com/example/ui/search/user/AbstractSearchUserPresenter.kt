package com.example.ui.search.user

import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.data.models.UserDetail.Companion.USER_ADDRESS_STREET
import com.example.data.models.UserDetail.Companion.USER_BINDS
import com.example.data.models.UserDetail.Companion.USER_LIMIT
import com.example.data.models.UserDetail.Companion.USER_OFFSET
import com.example.data.models.UserDetail.Companion.USER_SEARCH
import com.example.data.models.user.User
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.search.SearchPresenter
import com.example.ui.views.StateType
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit

abstract class AbstractSearchUserPresenter<V : SearchUserContract.View> constructor(
        private val appData: AppData,
        private val userRepository: UserRepository,
        private val commonRepository: CommonRepository,
        private val eventRepository: EventRepository
) : SearchPresenter<V, UserDetail, SearchFilter.UserNew>(appData), SearchUserContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        val data = mutableMapOf<String, Any>().apply {
            put(USER_LIMIT, limit)
            put(USER_OFFSET, offset)
            put(USER_BINDS, "userFavorite")
            val address = filter.address
            if (!address.isNullOrEmpty()) put(USER_ADDRESS_STREET, address)
            val interest = filter.spec ?: filter.theme
            if (interest != null) put(FILTER_INTEREST, interest)
            if (searchText.isNotEmpty()) put(USER_SEARCH, searchText.trim())
        }
        userRepository.getUsers(data).doOnSuccess {
            val uid = appData.getId()
            it.data.forEach { user -> user?.isCurrentUser = user?.id == uid }
        }
        /*userRepository.usersList(limit, offset, buildFilter())
                .doOnSuccess {
                    val uid = appData.getUser().user_id
                    it.data.forEach { user -> user?.isCurrentUser = user?.user_id == uid }
                }*/
    }

    private var isInterestsLoaded = false
    private var interests: Map<InterestNew/*Interest*/, List<InterestNew/*Interest*/>>? = null

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
                    .withLoadingDialog(viewState)
                    .subscribe({
                        showFilter()
                    }, {
                        showFilter()
                    })
        }
    }

    override fun onUserClick(user: UserDetail) {
        if (appData.isCurrentUser(user.id.toString())){
            viewState.showCurrentUser()
        }else {
            viewState.showUser(user)
        }
    }

    override fun onUserActionCLick(user: UserDetail) {
        val isSubscribed = user.binds?.userFavorite != null
        if (isSubscribed) {
            compositeDisposable += eventRepository.deleteFromFavorite(user.binds?.userFavorite?.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        user.binds?.userFavorite = null
                        viewState.updateUser(user)
                    }
        } else {
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, user.id)))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        user.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.updateUser(user)
                    }
        }
    }

    protected open fun buildFilter(): Map<String, Any> = mutableMapOf<String, Any>().apply {
        if (searchText.isNotEmpty()) put(FILTER_CONTENT, searchText)
        val address = filter.address
        if (!address.isNullOrEmpty()) put(FILTER_ADDRESS, address)
        val name = filter.name
        if (!name.isNullOrEmpty()) put(FILTER_NAME, name)
        val email = filter.email
        if (!email.isNullOrEmpty()) put(FILTER_EMAIL, email)
        val phone = filter.phone
        if (!phone.isNullOrEmpty()) put(FILTER_PHONE, phone)
        val favorites = filter.favorites
        if (favorites != null) put(FILTER_FAVORITES, favorites)
        val interest = filter.spec ?: filter.theme
        if (interest != null) put(FILTER_INTEREST, interest)

        val ageFrom = filter.ageFrom
        val ageTo = filter.ageTo
        if (ageFrom != null || ageTo != null) {
            val from = if (ageFrom == null || ageFrom < SEARCH_AGE_MIN) SEARCH_AGE_MIN
            else ageFrom
            val to = when {
                ageTo == null -> SEARCH_AGE_MAX
                ageTo < from -> from
                else -> ageTo
            }

            filter.ageFrom = from
            filter.ageTo = to
            put(FILTER_AGE, "$from-$to")
        }
    }

    override fun createFilter() = SearchFilter.UserNew()
    override fun copyFilter(filter: SearchFilter.UserNew) = filter.copy()

    companion object {
        private const val FILTER_CONTENT = "content"
        private const val FILTER_NAME = "user_fio"
        private const val FILTER_ADDRESS = "user_address"
        private const val FILTER_EMAIL = "user_email"
        private const val FILTER_PHONE = "user_phone"
        private const val FILTER_FAVORITES = "is_in_favorite"
        private const val FILTER_INTEREST = "interests"
        private const val FILTER_AGE = "user_age"

        private const val SEARCH_AGE_MIN = 14
        private const val SEARCH_AGE_MAX = 150
    }
}