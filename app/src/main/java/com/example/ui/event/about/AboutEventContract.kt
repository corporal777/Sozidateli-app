package com.example.ui.event.about

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy

interface AboutEventContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventData(
                eventData: EventData,
                userRegistration: Event.RegistrationStatus?,
                pages: List<EventPage>,
                partners: List<EventParther>,
                showContacts: Boolean
        )

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventName(name: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun showRegisterButton(show: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showPage(eventId: String, pageId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showDocuments(eventId: String, pageId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPartner(eventId: String, partnerId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showSpeakers(eventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showContacts(eventName: String, phones: List<PhoneAffiliation>, emails: List<EmailAffiliation>, webLinks: List<String>, socialLinks: List<String>, address: String?, mapInfo: MapInfo?, places: List<Place>?)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showLogoImage(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeEventSubscription(isSubscribed: Boolean)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "message")
        fun showWriteToOrganizationForm()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "message")
        fun hideWriteToOrganizationForm()
    }

    interface Presenter : BaseContract.Presenter {
        fun onContactsClick()
        fun onSpeakersClick()
        fun onGoToEventClick()
        fun onPageClick(page: EventPage)
        fun onPartnerClick(partner: EventParther)
        fun onLogoClick(url: String)
        fun onRefreshRequest()
        fun onShowFilterClick(format: Int)
        fun onChangeFavoriteClick()
        fun onWriteToOrganizationClick()
        fun onWriteToOrganizationMessage(message: String)
    }
}
