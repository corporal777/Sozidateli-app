package com.example.ui.event.speakers

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.MemberModel
import com.example.data.models.Speaker
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface EventSpeakersContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(data: List<MemberModel>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSpeaker(speaker: MemberModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSpeaker(speaker: MemberModel)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onSpeakerClick(speaker: MemberModel)
        fun onSpeakerFavoriteChangeClick(speaker: MemberModel)
        fun onRefreshRequest()
    }
}