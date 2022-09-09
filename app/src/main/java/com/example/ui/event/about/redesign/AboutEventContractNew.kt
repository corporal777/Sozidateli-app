package com.example.ui.event.about.redesign

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract

interface AboutEventContractNew {
    interface View : BaseContract.View {

        //@StateStrategyType(AddToEndSingleStrategy::class)
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEventData(
            eventData: EventNew?,
            pages: List<PageModel>?,
            members: List<MemberModel>?,
            partners: List<PartnerModel>?,
            tags: List<Tag>
        )

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setActionButton(
            event: EventNew?,
            userRegistration: Event.Status?
        )


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

        @StateStrategyType(SkipStrategy::class)
        fun showEventAddedToFavoriteMessage()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeOrganizationSubscription(isSubscribed: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showShare(eventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventActivities(eventId: String, listTags: List<NewTags>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSpeakerProfile(speakerId: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showMap(mapInfo: MapInfo?)

        @StateStrategyType(SkipStrategy::class)
        fun showErrorMessageWithResult(withResult: Boolean, eventId: String, message: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubEvents(isApproved: Boolean, subEvents: Map<String, List<EventActivityModel>>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onGoToEventClick()
        fun onPageClick(page: Int/*EventPage*/)
        fun onPartnerClick(partner: Int/*EventParther*/)
        fun onRefreshRequest()

        fun onOrganizationClick(organization: String)

        fun onActionCancel()
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
    }
}
