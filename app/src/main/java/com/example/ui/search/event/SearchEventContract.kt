package com.example.ui.search.event

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.search.SearchContract

interface SearchEventContract {
    interface View : SearchContract.View<EventNew, SearchFilter.EventNew> {
        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showAgreementRegisterDialog(event: String, url : String)
    }

    interface Presenter : SearchContract.Presenter<EventNew> {
        fun onActionRegister(event: String, url : String?)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(event: String)
        fun onAcceptRegistrationAgreement(event : String)
    }
}
