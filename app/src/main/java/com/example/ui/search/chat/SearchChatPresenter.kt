package com.example.ui.search.chat

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.extensions.buildList
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class SearchChatPresenter
@Inject constructor(
    val appData: AppData,
    val userRepository: UserRepository,
    val commonRepository: CommonRepository
) : BasePresenter<SearchChatContract.View>(appData), SearchChatContract.Presenter {

    private var isDataLoadWithFilter = false
    private var filter = SearchFilter.UserNew()
    private var mSearchText = ""
    private lateinit var paginationList: PaginationList<UserDetail?>
    private var mDy = 0f

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initData()
        initInterest()
    }

    override fun attachView(view: SearchChatContract.View?) {
        super.attachView(view)
        viewState.changeAppBarElevation(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.changeAppBarElevation(mDy)
    }

    private fun initInterest() {
        compositeDisposable += commonRepository.getInterests()
            .map { interests ->
                interests.groupByNotNull { child -> interests.firstOrNull { it.id == child.parent } }
            }
            .performOnBackgroundOutOnMain()
            .subscribe({
                this.filter.interests = it
            }, {
                it.printStackTrace()
            })
    }


    override fun onUserClick(user: UserDetail) {
        if (appData.isCurrentUser(user.id.toString())){
            viewState.showCurrentUser()
        }else {
            viewState.showUser(user)
        }
    }
    override fun onFilterClick() = viewState.showFilter(filter)
    override fun onFilterApplyClick() = initData()
    override fun onRefreshRequest() = paginationList.invalidate()
    override fun onSearchTextChange(text: String) {
        mSearchText = text
        initData()
    }

    private fun initData() {
        viewState.setUsersData(List(20) { null })
        if (!::paginationList.isInitialized) {
            paginationList = pagination.applyErrorHandler {
                it.printStackTrace()
            }
                .buildList(enablePlaceholders = false)
        }

        compositeDisposable += Observable.create(paginationList)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isNullOrEmpty()) {
                    viewState.showEmptyDataPlaceholder()
                } else {
                    viewState.setUsersData(it)
                    //dispatchListUpdate(it)
                }
            }
    }

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        val data = mutableMapOf<String, Any>().apply {
            put(UserDetail.USER_LIMIT, limit)
            put(UserDetail.USER_OFFSET, offset)
            put(UserDetail.USER_BINDS, "userFavorite")
            val address = filter.address
            if (!address.isNullOrEmpty()) put(UserDetail.USER_ADDRESS_STREET, address)
            val interest = filter.spec ?: filter.theme
            if (interest != null) put(FILTER_INTEREST, interest)
            if (mSearchText.isNotEmpty()) put(UserDetail.USER_SEARCH, mSearchText.trim())
        }
        userRepository.getUsers(data).doOnSuccess {
            val uid = appData.getId()
            it.data.forEach { user -> user?.isCurrentUser = user?.id == uid }
        }
    }

    override fun onItemTake(position: Int) = paginationList.onItemTake(position)

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