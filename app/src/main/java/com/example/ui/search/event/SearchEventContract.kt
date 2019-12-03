package com.example.ui.search.event

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract

interface SearchEventContract {
    interface View : SearchContract.View<Event, SearchFilter.Event> {
        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganizationEmails(emails: List<EmailAffiliation>)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganization(email: EmailAffiliation)

        @StateStrategyType(SkipStrategy::class)
        fun selectEvent()
    }

    interface Presenter : SearchContract.Presenter<Event> {
        fun onActionRegister(event: Event)
        fun onActionCancel(event: Event)
        fun onActionWriteToOrganization(event: Event)
        fun onActionShowEvent(event: Event)
        fun onShowEventClick(event: Event)
        fun onShowFormatClick(event: Event)
        fun onWriteToOrganizationEmailChosen(email: EmailAffiliation)
    }
}
