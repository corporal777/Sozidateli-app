package com.example.ui.search.event

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchEventContract {
    interface View : SearchContract.View, BaseContract.LoadingEventView {

        @OneExecution
        fun setData(data: PagingData<EventNew>, isTemporary : Boolean)

        @OneExecution
        fun showAboutEvent(event: String)

        @OneExecution
        fun showEventRequest(event: String)

        @OneExecution
        fun showAuthorization()

        @OneExecution
        fun showQrScanner()

        @Skip
        fun showFilter(filter: SearchFilter.EventNew)

        @Skip
        fun updateEvent(event: EventNew)
    }

    interface Presenter : SearchContract.Presenter {
        fun onActionRegister(event: EventNew, withAccept : Boolean, position : Int)
        fun onActionCancel(event: EventNew, position : Int)
        fun onShowEventClick(event: String)
        fun onShowAuthorization(event: String)
        fun onScanClick()
        fun onFiltersApplyClick(filter: SearchFilter.EventNew)
    }
}
