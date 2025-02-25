package com.example.ui.organizations.events

import androidx.paging.PagingData
import com.example.data.models.EventNew
import com.example.ui.event.list.EventListContract
import moxy.viewstate.strategy.alias.OneExecution

interface OrganizationEventsContract {
    interface View : EventListContract.View {
        @OneExecution
        fun setData(data: PagingData<EventNew>, isTemporary : Boolean)
    }

    interface Presenter : EventListContract.Presenter {

    }
}
