package com.example.ui.search.event

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchEventContract {
    interface View : SearchContract.View<SearchFilter.EventNew> {
        @OneExecution
        fun setData(data: PagingData<EventNew>, isTemporary : Boolean)

        @OneExecution
        fun updateEvent(event: EventNew)

        @OneExecution
        fun showAboutEvent(event: String)

        @OneExecution
        fun showEventRequest(event: String)

        @OneExecution
        fun showAuthorization()

        @OneExecution
        fun showQrScanner()

        @Skip
        fun showAgreementRegisterDialog(event: EventNew)
    }

    interface Presenter : SearchContract.Presenter<SearchFilter.EventNew> {
        fun onActionRegister(event: EventNew, withRegister : Boolean)
        fun onActionCancel(event: EventNew)
        fun onShowEventClick(event: String)
        fun onShowAuthorization(event: String)
        fun onScanClick()
    }
}
