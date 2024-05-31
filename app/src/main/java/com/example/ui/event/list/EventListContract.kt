package com.example.ui.event.list

import com.example.data.models.EventNew
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventListContract {
    interface View : BaseContract.View {
        @Skip
        fun showEventRequest(event: String)

        @Skip
        fun showAboutEvent(event: String)

        @Skip
        fun showAuthorization()

        @Skip
        fun showAgreementRegisterDialog(event: String, url : String, formEnabled : Boolean)

        @Skip
        fun showEventRegistrationSuccessDialog()

        @OneExecution
        fun updateEvent(event: EventNew)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onActionRegister(event: String, url : String?, formEnabled : Boolean)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(event: String)
        fun onShowAuthorization(event: String)
        fun onAcceptRegistrationAgreement(event : String, formEnabled : Boolean)
    }
}
