package com.example.ui.search.tabs

import com.example.data.AppData
import com.example.data.models.SearchFilter
import com.example.ui.base.BasePresenter
import com.example.ui.search.SearchInterface
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class SearchTabsPresenter
@Inject constructor(
    appData: AppData,
) : BasePresenter<SearchTabsContract.View>(appData), SearchTabsContract.Presenter {

    lateinit var searchInterface: SearchInterface
    var filter: SearchFilter? = null


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