package com.example.ui.event.my.schedule

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.ui.base.BaseContract

interface MyScheduleEventsContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setHeaderAndCalendar(month : String, days: List<EventScheduleCalendarDay>?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSearchBlock()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContent(data: List<EventNew?>)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)
    }

    interface Presenter : BaseContract.Presenter{
        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
    }
}