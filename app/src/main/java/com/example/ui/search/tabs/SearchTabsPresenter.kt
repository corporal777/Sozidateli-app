package com.example.ui.search.tabs

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import com.example.ui.search.SearchInterface
import javax.inject.Inject

@InjectViewState
class SearchTabsPresenter
@Inject constructor(
) : BasePresenter<SearchTabsContract.View>(), SearchTabsContract.Presenter {

    lateinit var searchInterface: SearchInterface

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

    override fun onDestroy() {
        super.onDestroy()
        searchInterface.showFilterCallback = null
        searchInterface.searchTextCallback = null
    }
}