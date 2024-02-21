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
        fun showPage(eventId: String, pageId: String)

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
        fun showEventFormResult(formResult: UserFormResultModel)

        @OneExecution
        fun showMap(mapInfo: MapInfo?)

        @Skip
        fun setActionButton(event: EventNew?)

        @Skip
        fun setEventFavoriteButton(isSubscribed: Boolean)

        @Skip
        fun changeOrganizationSubscription(isSubscribed: Boolean)

        @Skip
        fun showShare(eventId: String)

        @Skip
        fun updateSubEvent(subEvent: EventActivityModel)

        @Skip
        fun updateTags(tag: Tag)

        @Skip
        fun showErrorMessageWithResult(withResult: Boolean, eventId: String, message: String)

        @Skip
        fun showAgreementRegisterDialog(event: String, url: String)

        @Skip
        fun addEventToCalendar(eventData: EventNew?)

        @Skip
        fun updateAppBarBackgroundColorValue(value: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPageClick(page: Int)
        fun onPartnerClick(partner: Int)
        fun onRefreshRequest()

        fun onOrganizationClick(orgId: String)

        fun onActionRegister( url: String?)
        fun onActionCancel()
        fun onAcceptRegistrationAgreement(event: String)
        fun onShareClick()
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onSubEventClick(subEvent: EventActivityModel)
        fun onSpeakerClick(memberId: Int)
        fun onShowAllSpeakersClick()
        fun onMapPageSelected()
        fun onTagSelected()

        fun onShowEventActivitiesClick()
        fun onAddOrganizationToFavoriteClick()
        fun onAddEventToFavoriteClick()
        fun onCreateEventSubscriptionClick()
        fun onDeleteEventSubscriptionClick()
        fun changeAppBarBackgroundColorValue(value: Int)

        fun onAddEventToCalendarClick()

        fun onShowFormResult()
    }
}
