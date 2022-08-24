package com.example.ui.subevent

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract

interface SubEventContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(isApproved : Boolean, subEvent: EventActivityModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSpeakers(speakers: List<MemberModel>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSpeakerProfile(speaker: MemberModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEventErrorMessageDialog(withResult : Boolean, id : String, message : String)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun onSpeakerClick(speaker: MemberModel)
//        fun onSpeakerChangeSubscriptionClick(speaker: MemberModel)
//        fun onSubeventChangeSubscriptionClick(subevent: EventActivityModel)

        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)    }
}
