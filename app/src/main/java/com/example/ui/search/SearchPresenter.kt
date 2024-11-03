package com.example.ui.search

import android.util.Log
import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.extensions.buildList
import com.example.ui.base.BasePresenter
import com.example.util.PAGE_SIZE
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomLoading
import withDelay
import withLoadingDialog
import withProgressBarDialogLoading

abstract class SearchPresenter<V : SearchContract.View<I, F>, I : Any, F : SearchFilter>(appData: AppData) :
    BasePresenter<V>(appData), SearchContract.Presenter<I> {


    private lateinit var searchInterface: SearchInterface

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
        viewState.setHasFilter()
    }

    override fun onItemTake(position: Int) {

    }
    override fun onRefreshRequest() {

    }
    override fun onFilterClearClick() = viewState.clearFilter()
    protected open fun onShowFilterRequest() = viewState.showFilter(tmpFilter)

    override fun onFilterApplyClick() {
        filter = copyFilter(tmpFilter)
        invalidateList()
        viewState.hideFilter()
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
    }


    abstract fun createFilter(): F
    abstract fun copyFilter(filter: F): F
}