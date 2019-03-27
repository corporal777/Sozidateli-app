package com.example.ui.myEvents

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.EventRegisterResponse
import com.example.ui.base.BaseContract

interface MyEventsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(events: PagedList<EventRegisterResponse>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun selectEvent(event: Event)
    }

    interface Presenter : BaseContract.Presenter {
        fun onEventClick(event: Event)
        fun onScrollChange(position: Int, offset: Int)
    }
}
