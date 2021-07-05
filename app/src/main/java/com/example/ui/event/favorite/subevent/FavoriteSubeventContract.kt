package com.example.ui.event.favorite.subevent

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.SubEvent
import com.example.ui.base.BaseContract

interface FavoriteSubeventContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(data: Map<Long?, List</*SubEvent*/EventActivityModel>>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangeFavoriteRequest(subevent: /*SubEvent*/EventActivityModel)
        fun onSubEventClick(subEvent: /*SubEvent*/EventActivityModel)
        fun onRefreshRequest()
    }
}