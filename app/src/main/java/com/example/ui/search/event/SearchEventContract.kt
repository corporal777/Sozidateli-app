package com.example.ui.search.event

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.search.SearchContract

interface SearchEventContract {
    interface View : SearchContract.View<EventNew, SearchFilter.EventNew/*Event*/> {
        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganizationEmails(emails: List<EventPhoneModel/*EmailAffiliation*/>)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganization(email: EventPhoneModel/*EmailAffiliation*/)

        @StateStrategyType(SkipStrategy::class)
        fun selectEvent()
    }

    interface Presenter : SearchContract.Presenter<EventNew/*Event*/> {
        fun onActionRegister(event: String)
        fun onActionCancel(event: String, registrationId: String?)
        fun onActionWriteToOrganization(emails: List<EventPhoneModel/*EmailAffiliation*/>)
        fun onActionShowEvent(event: String)
        fun onShowEventClick(event: String)
        fun onShowFormatClick(format: Int)
        fun onWriteToOrganizationEmailChosen(email: EventPhoneModel/*EmailAffiliation*/)
    }
}
