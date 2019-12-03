package com.example.ui.search.user

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Interest
import com.example.data.models.SearchFilter
import com.example.data.models.user.User
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SearchUserPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val commonRepository: CommonRepository
) : SearchPresenter<SearchUserContract.View, User, SearchFilter.User>(), SearchUserContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        userRepository.usersList(limit, offset, buildFilter())
    }

    private var isInterestsLoaded = false
    private var interests: Map<Interest, List<Interest>>? = null

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

    override fun onUserClick(user: User) {
        viewState.showUser(user)
    }

    override fun onUserActionCLick(user: User) {
        val id = user.user_id.toString()
        val request = if (user.is_in_favorite) userRepository.removeFromFavorite(id)
        else userRepository.addToFavorite(id)
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    user.is_in_favorite = !user.is_in_favorite
                    viewState.updateUser(user)
                }
    }

    private fun buildFilter(): Map<String, Any> = mutableMapOf<String, Any>().apply {
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

    override fun createFilter() = SearchFilter.User()
    override fun copyFilter(filter: SearchFilter.User) = filter.copy()

    companion object {
        private const val FILTER_CONTENT = "content"
        private const val FILTER_NAME = "user_fio"
        private const val FILTER_ADDRESS = "user_address"
        private const val FILTER_EMAIL = "user_email"
        private const val FILTER_PHONE = "user_phone"
        private const val FILTER_FAVORITES = "is_in_favorite"
        private const val FILTER_INTEREST = "interest"
        private const val FILTER_AGE = "user_age"

        private const val SEARCH_AGE_MIN = 14
        private const val SEARCH_AGE_MAX = 150
    }
}