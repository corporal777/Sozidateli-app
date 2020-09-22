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
                showContacts: Boolean,
                userAgreement: String?
        )

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setActionButton(
                event: EventData,
                userRegistration: Event.RegistrationStatus?
        )

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventName(name: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPage(eventId: String, pageId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPartner(eventId: String, partnerId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showSpeakers(eventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showContacts(eventName: String, phones: List<PhoneAffiliation>, emails: List<EmailAffiliation>, webLinks: List<String>, socialLinks: List<String>, address: String?, place: String?, mapInfo: MapInfo?, places: List<Place>?)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showLogoImage(url: String)

        @StateStrategyType(SkipStrategy::class)
        fun showRating(eventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showAgreement(url: String)

        @StateStrategyType(SkipStrategy::class)
        fun showOrganization(organization: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeEventSubscription(isSubscribed: Boolean)

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "message")
        fun showWriteToOrganizationForm()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "message")
        fun hideWriteToOrganizationForm()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWriteToOrganizationComplete()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWriteToOrganizationError()

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganization(email: EmailAffiliation)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganizationEmails(emails: List<EmailAffiliation>)

        @StateStrategyType(SkipStrategy::class)
        fun selectEvent()

        @StateStrategyType(SkipStrategy::class)
        fun showRegistrationFieldsRequest(fields: List<String>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEditProfile(id: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onContactsClick()
        fun onSpeakersClick()
        fun onGoToEventClick()
        fun onSelectEventClick()
        fun onPageClick(page: EventPage)
        fun onPartnerClick(partner: EventParther)
        fun onLogoClick()
        fun onRefreshRequest()
        fun onShowFilterClick(format: Int)
        fun onChangeFavoriteClick()
        fun onWriteToOrganizationClick()
        fun onWriteToOrganizationMessage(message: String)
        fun onRateClick()
        fun onAgreementClick()
        fun onOrganizationClick(organization: String)

        fun onActionCancel()
        fun onActionWriteToOrganization()
        fun onWriteToOrganizationEmailChosen(email: EmailAffiliation)

        fun onShowEditProfileClick()
    }
}
