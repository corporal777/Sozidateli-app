package com.example.ui.event.speakers

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface UserSpeakerContract{
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName: String, userAvatar: String?, chatId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSpeakerMainInfo(speaker: MemberModel, isCurrentUser : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSpeaker(speaker: UserDetail)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSpeakerActivities(data : List<EventActivityModel>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmptyEventsPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmptyMainDataPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onWriteMessageClick(speaker: UserDetail)
        fun onAddSpeakerToFavoriteClick(id: String)

        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
        fun onSubEventClick(subEvent: EventActivityModel)
    }
}