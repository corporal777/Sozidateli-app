package com.example.ui.event.schedule

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.Tag
import com.example.holders.SubEventItem
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface EventScheduleContract {

    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setTags(tags: List<Tag>?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setDays(days: List<EventScheduleCalendarDay>?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectDay(day: EventScheduleCalendarDay)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToDay(day: EventScheduleCalendarDay)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubEvents(subEvents: PagedList<SubEventItem>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyEventPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyDayPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun hidePlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "currentDay")
        fun showCurrentDay(day: EventScheduleCalendarDay)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "currentDay")
        fun hideCurrentDay()

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: Int, subeventId: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onDaySelected(day: EventScheduleCalendarDay)
        fun onTagSelectedListChange(tags: List<Tag>)
        fun onDayChanged(date: Long)
    }
}
