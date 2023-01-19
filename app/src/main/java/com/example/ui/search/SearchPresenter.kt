package com.example.ui.search

import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.data.models.SearchUserData
import com.example.data.models.UserDetail
import com.example.extensions.buildList
import com.example.ui.base.BasePresenter
import com.example.ui.search.user.AbstractSearchUserPresenter
import com.example.util.pagination.flow.PaginationListFlow
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class SearchPresenter<V : SearchContract.View<I, F>, I, F : SearchFilter>(appData: AppData) : BasePresenter<V>(appData), SearchContract.Presenter<I> {

    private lateinit var paginationList: PaginationList<I?>
    private lateinit var searchInterface: SearchInterface
    protected abstract val pagination: PaginationDataSourceFactory<I?>
    protected lateinit var searchText: String
    protected lateinit var filter: F
    protected lateinit var tmpFilter: F

    private val searchDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        filter = createFilter()
        tmpFilter = createFilter()
        compositeDisposable += searchDisposable
    }

    override fun onResume(searchInterface: SearchInterface) {
        this.searchInterface = searchInterface.apply {
            searchTextCallback = { onSearchTextChange(searchText) }
            showFilterCallback = { onShowFilterRequest() }
            onSearchTextChange(searchText)
        }
    }

    override fun onItemTake(position: Int) {
        paginationList.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }

    override fun onFilterApplyClick() {
        filter = copyFilter(tmpFilter)
        invalidateList()
        viewState.hideFilter()
    }

    override fun onFilterClearClick() {
        viewState.clearFilter()
    }

    protected open fun onShowFilterRequest() {
        viewState.showFilter(tmpFilter)
    }

    override fun onFilterCancel() {
        tmpFilter = copyFilter(filter)
    }

    private fun onSearchTextChange(text: String) {
        val isReallyChange = !::searchText.isInitialized || searchText != text
        this.searchText = text
        if (isReallyChange) invalidateList()
    }

    private fun invalidateList() {
        searchDisposable.clear()
        initSearchPagination()
    }

    private fun initSearchPagination() {
        if (!::paginationList.isInitialized) {
            paginationList = pagination.applyErrorHandler {
                it.printStackTrace()
            }
                    .buildList(enablePlaceholders = false)
        }

        if (searchDisposable.size() == 0) {
            viewState.setData(List(20) { null })
            searchDisposable += Observable.create(paginationList)
                    .performOnBackgroundOutOnMain()
                    .subscribeSimple { onDataLoaded(it) }
        }
    }

    protected open fun onDataLoaded(data: List<I?>) {
        viewState.setData(data)
    }

    abstract fun createFilter(): F
    abstract fun copyFilter(filter: F): F
}