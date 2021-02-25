package com.example.ui.search.event

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.data.models.EventPhoneModel
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract

interface SearchEventContract {
    interface View : SearchContract.View<Event, SearchFilter.Event> {
        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganizationEmails(emails: List<EventPhoneModel>)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganization(email: EventPhoneModel)

        @StateStrategyType(SkipStrategy::class)
        fun selectEvent()
    }

    interface Presenter : SearchContract.Presenter<Event> {
        fun onActionRegister(event: String)
        fun onActionCancel(event: String)
        fun onActionWriteToOrganization(emails: List<EventPhoneModel>)
        fun onActionShowEvent(event: String)
        fun onShowEventClick(event: String)
        fun onShowFormatClick(format: Int)
        fun onWriteToOrganizationEmailChosen(email: EventPhoneModel)
    }
}
