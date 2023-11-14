package com.example.ui.search.tabs

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface SearchTabsContract {
    interface View : BaseContract.View {
        @OneExecution
        fun showQrScanner()
    }

    interface Presenter : BaseContract.Presenter {
        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
        fun onFilterClick()
        fun onScanClick()
    }
}
