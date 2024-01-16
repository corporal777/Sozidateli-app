package com.example.ui.event.about

import com.example.data.models.*
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface AboutEventContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setEventDataPlaceholder()

        @AddToEndSingle
        fun setMainData(event: EventNew)

        @AddToEndSingle
        fun setOrganizationData(event: EventNew)

        @AddToEndSingle
        fun setSpeakersData(speakers: List<MemberModel>, showMore : Boolean)

        @AddToEndSingle
        fun setProgramData(eventData: AboutEventData)

        @AddToEndSingle
        fun setPartnersData(partners: List<PartnerModel>)

        @AddToEndSingle
        fun setActionButton(event: EventNew?)

        @Skip
        fun showPage(eventId: String, pageId: String)

        @Skip
        fun showPartner(eventId: String, partnerId: String)

        @Skip
        fun showSpeakers(eventId: String)

        @Skip
        fun showEventRequest(event: String)

        @Skip
        fun showOrganization(organization: String)

        @OneExecution
        fun changeEventSubscription(isSubscribed: Boolean)

        @OneExecution
        fun changeOrganizationSubscription(isSubscribed: Boolean)

        @Skip
        fun showShare(eventId: String)

        @OneExecution
        fun updateSubEvent(subEvent: EventActivityModel)

        @OneExecution
        fun updateTags(tag: Tag)

        @Skip
        fun showSubEvent(eventId: String, subEventId: String)

        @Skip
        fun showEventActivities(eventId: String, listTags: List<NewTags>)

        @Skip
        fun showSpeakerProfile(speakerId: Int, eventId: String)

        @Skip
        fun showMap(mapInfo: MapInfo?)

        @Skip
        fun showErrorMessageWithResult(withResult: Boolean, eventId: String, message: String)

        @Skip
        fun showAgreementRegisterDialog(event: String, url : String)

        @OneExecution
        fun addEventToCalendar(eventData : EventNew?)

        @Skip
        fun updateAppBarBackgroundColorValue(value : Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPageClick(page: Int)
        fun onPartnerClick(partner: Int)
        fun onRefreshRequest()

        fun onOrganizationClick(organization: String)

        fun onActionRegister(url : String?)
        fun onActionCancel()
        fun onAcceptRegistrationAgreement(event : String)
        fun onShareClick()
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
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
        fun changeAppBarBackgroundColorValue(value : Int)

        fun onAddEventToCalendarClick()
    }
}
