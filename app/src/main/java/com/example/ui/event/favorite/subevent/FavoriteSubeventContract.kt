package com.example.ui.event.favorite.subevent

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SubEvent
import com.example.ui.base.BaseContract

interface FavoriteSubeventContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(data: Map<Long?, List<SubEvent>>)
    }

    interface Presenter : BaseContract.Presenter
}