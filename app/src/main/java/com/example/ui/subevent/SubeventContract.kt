package com.example.ui.subevent

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract

interface SubeventContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(subEvent: EventActivityModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSpeakers(speakers: List<MemberModel>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSpeakerProfile(speaker: MemberModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSpeaker(speaker: MemberModel)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSpeakerClick(speaker: MemberModel)
        fun onSpeakerChangeSubscriptionClick(speaker: MemberModel)
        fun onSubeventChangeSubscriptionClick(subevent: EventActivityModel)
    }
}
