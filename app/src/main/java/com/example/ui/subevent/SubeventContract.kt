package com.example.ui.subevent

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Speaker
import com.example.data.models.SubeventInfo
import com.example.ui.base.BaseContract

interface SubeventContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(subEvent: SubeventInfo)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSpeakerProfile(speaker: Speaker)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSpeaker(speaker: Speaker)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openUserList(eventId: Int, subEventId: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSpeakerClick(speaker: Speaker)
        fun onSpeakerChangeSubscriptionClick(speaker: Speaker)
        fun onOpenUserListClick()
    }
}
