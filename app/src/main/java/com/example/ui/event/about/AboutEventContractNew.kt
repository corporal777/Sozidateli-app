package com.example.ui.event.about

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract
import com.example.data.models.AboutEventData

interface AboutEventContractNew {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEventDataPlaceholder()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setMainData(event: EventNew)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setOrganizationData(event: EventNew)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setSpeakersData(speakers: List<MemberModel>, showMore : Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setProgramData(eventData: AboutEventData)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setPartnersData(partners: List<PartnerModel>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setActionButton(event: EventNew?)

        @StateStrategyType(SkipStrategy::class)
        fun showPage(eventId: String, pageId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showPartner(eventId: String, partnerId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showSpeakers(eventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showOrganization(organization: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeEventSubscription(isSubscribed: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeOrganizationSubscription(isSubscribed: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showShare(eventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateTags(tag: Tag)

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventActivities(eventId: String, listTags: List<NewTags>)

        @StateStrategyType(SkipStrategy::class)
        fun showSpeakerProfile(speakerId: Int, eventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showMap(mapInfo: MapInfo?)

        @StateStrategyType(SkipStrategy::class)
        fun showErrorMessageWithResult(withResult: Boolean, eventId: String, message: String)

        @StateStrategyType(SkipStrategy::class)
        fun showAgreementRegisterDialog(event: String, url : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun addEventToCalendar(eventData : EventNew?)

        @StateStrategyType(SkipStrategy::class)
        fun updateAppBarBackgroundColorValue(value : Int)

        @StateStrategyType(SkipStrategy::class)
        fun showTest()
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
