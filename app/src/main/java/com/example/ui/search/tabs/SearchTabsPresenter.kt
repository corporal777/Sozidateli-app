package com.example.ui.search.tabs

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.data.models.SearchUserData
import com.example.data.models.UserDetail
import com.example.extensions.buildListNew
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.search.SearchInterface
import com.example.ui.search.user.AbstractSearchUserPresenter
import com.example.util.pagination.flow.PaginationListFlow
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

@InjectViewState
class SearchTabsPresenter
@Inject constructor(
    appData: AppData,
    private val userRepository: UserRepository,
) : BasePresenter<SearchTabsContract.View>(appData), SearchTabsContract.Presenter {

    lateinit var searchInterface: SearchInterface
    var filter: SearchFilter? = null
    var currentPosition = 0


    private lateinit var paginationListNew: PaginationListFlow<SearchUserData?>
    private val paginationNew = PaginationDataSourceFactory { limit, offset ->
        val data = mutableMapOf<String, Any>().apply {
            put(AbstractSearchUserPresenter.SEARCH_USER_TYPE, true)
            put(UserDetail.USER_LIMIT, limit)
            put(UserDetail.USER_OFFSET, offset)
        }
        userRepository.searchUsersNew(data)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        paginationListNew = paginationNew.applyErrorHandler {}
            .buildListNew(enablePlaceholders = false)
        compositeDisposable += Flowable.create(paginationListNew, BackpressureStrategy.BUFFER)
            .subscribeSimple {
                Log.e("USERS", it.toString())
            }
    }

    override fun attachView(view: SearchTabsContract.View?) {
        super.attachView(view)
        viewState.setCurrentFragment(currentPosition)
    }


    override fun onSearchTextChange(text: String) {
        onSearchTextSubmit(text)
    }

    override fun onSearchTextSubmit(text: String) {
        searchInterface.apply {
            searchText = text
            searchTextCallback?.invoke()
        }
    }

    override fun onFilterClick() {
        searchInterface.showFilterCallback?.invoke()
    }

    override fun onScanClick() {
        viewState.showQrScanner()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchInterface.showFilterCallback = null
        searchInterface.searchTextCallback = null
    }
}