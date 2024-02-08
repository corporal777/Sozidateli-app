package com.example.ui.event.speakers.member

import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserSpeakerContract{
    interface View : BaseContract.View {

        @OneExecution
        fun openChat(userName: String, userAvatar: String?, chatId: String)

        @OneExecution
        fun setSpeakersMainInfo(speaker: MemberModel, isCurrentUser : Boolean)

        @OneExecution
        fun updateSpeaker(speaker: UserDetail)

        @OneExecution
        fun setSpeakerActivities(canShow : Boolean, data: Map<String?, List<EventActivityModel>>?)


        @OneExecution
        fun setEmptyMainDataPlaceholder()

        @OneExecution
        fun updateSubEvent(subEvent: EventActivityModel)

        @Skip
        fun showSubEvent(eventId: String, subEventId: String)

        @Skip
        fun showUserProfile(userId: String)

        @Skip
        fun showCurrentUserProfile()
    }

    interface Presenter : BaseContract.Presenter {
        fun onWriteMessageClick()
        fun onAddSpeakerToFavoriteClick()

        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
        fun onSubEventClick(subEvent: EventActivityModel)
        fun onGoToProfileClick()
    }
}