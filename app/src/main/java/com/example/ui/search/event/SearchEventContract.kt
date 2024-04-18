package com.example.ui.search.event

import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchEventContract {
    interface View : SearchContract.View<EventNew, SearchFilter.EventNew> {
        @OneExecution
        fun showAboutEvent(event: String)

        @OneExecution
        fun showEventRequest(event: String)

        @Skip
        fun showAgreementRegisterDialog(event: String, url : String, formEnabled: Boolean)

        @Skip
        fun showEventRegistrationSuccessDialog()
    }

    interface Presenter : SearchContract.Presenter<EventNew> {
        fun onActionRegister(event: String, url : String?, formEnabled : Boolean)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(event: String)
        fun onAcceptRegistrationAgreement(event : String, formEnabled: Boolean)
    }
}
