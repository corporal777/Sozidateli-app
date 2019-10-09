package com.example.ui.search.tabs

import com.example.ui.base.BaseContract

interface SearchTabsContract {
    interface View : BaseContract.View

    interface Presenter : BaseContract.Presenter {
        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
        fun onFilterClick()
    }
}
