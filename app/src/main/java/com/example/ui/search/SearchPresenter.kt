package com.example.ui.search

import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.exceptions.EmptyDataException
import com.example.ui.base.BasePresenter

abstract class SearchPresenter<V : SearchContract.View<F>, F : SearchFilter>(appData: AppData) :
    BasePresenter<V>(appData), SearchContract.Presenter<F> {


    private lateinit var searchInterface: SearchInterface
    private var isFirstLaunch = true

    protected var searchText: String = ""

    override fun attachView(view: V) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else viewState.invalidatePagingData()
    }

    override fun onResume(searchInterface: SearchInterface) {
        this.searchInterface = searchInterface.apply {
            searchTextCallback = { onSearchTextChange(searchText) }
            showFilterCallback = { onShowFilterRequest() }
            onSearchTextChange(searchText)
        }
        viewState.setHasFilter()
    }

    private fun onSearchTextChange(text: String) {
        val isReallyChange = searchText != text
        searchText = text
        if (isReallyChange) onRefreshRequest()
    }
}