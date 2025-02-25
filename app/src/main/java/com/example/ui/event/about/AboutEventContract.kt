package com.example.ui.event.about

import android.content.Context
import com.example.data.models.*
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface AboutEventContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setEventData(eventData: AboutEventData)

        @OneExecution
        fun showPartner(eventId: String, partnerId: String)

        @OneExecution
        fun showSpeakers(eventId: String)

        @OneExecution
        fun showEventRequest(event: String)

        @OneExecution
        fun showOrganization(organization: String)

        @OneExecution
        fun showSubEvent(eventId: String, subEventId: Int?)

        @OneExecution
        fun showEventActivities(eventId: String, listTags: List<NewTags>)

        @OneExecution
        fun showSpeakerProfile(speakerId: Int, eventId: String)

        @OneExecution
        fun showAuthorization()


        @Skip
        fun setActionButton(event: EventNew?)

        @Skip
        fun setEventFavoriteButton(isSubscribed: Boolean)

        @Skip
        fun updateOrganization(isSubscribed: Boolean)

        @Skip
        fun updateSubEvent(subEvent: EventActivityModel)

        @Skip
        fun updateTags(tag: Tag)

        @Skip
        fun showShare(eventId: String)

        @Skip
        fun showErrorMessageWithResult(with: Boolean, eventId: String, message: String)

        @Skip
        fun showAgreementRegisterDialog(event: String, url: String)

        @Skip
        fun showEventSubscribedDialog(isSubscribed: Boolean?)

        @Skip
        fun addEventToCalendar(eventData: EventNew?)

        @Skip
        fun updateAppBarBackgroundColor(value: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPartnerClick(partner: Int)
        fun onRefreshRequest()

        fun onOrganizationClick(orgId: String)

        fun onActionRegister(withAccept : Boolean)
        fun onActionCancel()

        fun onShareClick()
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onSubEventClick(subEvent: Int)
        fun onSpeakerClick(memberId: Int)
        fun onShowAllSpeakersClick()

        fun onTagSelected()

        fun onShowSubEventsClick()
        fun onAddOrganizationToFavoriteClick()
        fun onAddEventToFavoriteClick()
        fun onSubscribeEvent(isSubscribed: Boolean)

        fun onChangeAppBarBackgroundColor(value: Int)

        fun onAddEventToCalendarClick()


        fun onShowAuthorization()
    }
}
