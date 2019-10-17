package com.example.ui.event.about

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.EventPage
import com.example.data.models.Partner
import com.example.ui.base.BaseContract

interface AboutEventContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventData(
                logo: String?,
                organizationName: String?,
                dates: String?,
                description: String?,
                pages: List<EventPage>,
                partners: List<Partner>
        )

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventName(name: String)

        @StateStrategyType(SkipStrategy::class)
        fun showDocuments(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showContacts(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showPartner(partner: Partner)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)
    }

    interface Presenter : BaseContract.Presenter {
        fun onContactsClick()
        fun onSpeakersClick()
        fun onGoToEventClick()
        fun onPageClick(page: EventPage)
        fun onPartnerClick(partner: Partner)
    }
}
