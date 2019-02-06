package com.example.ui.search.searchType

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SearchTypeEvent
import com.example.ui.base.BaseContract

interface SearchTypeContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(data: MutableList<SearchTypeEvent>)

    }

    interface Presenter : BaseContract.Presenter {
        fun save()
        fun selectItem(isSelect: Boolean, item: SearchTypeEvent)
        fun setData(data: MutableList<SearchTypeEvent>, isPlaces: Boolean)
    }
}
