package com.example.ui.event.activities

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.EventScheduleDay
import com.example.data.models.Tag
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ActivitiesContract {

    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContentPlaceholder()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setDays(days: Map<Int, List<EventScheduleDay>>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTags(tags: List<Tag>?)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setSubEvents(isApproved : Boolean, data: List<SubEventsData>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyEventPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectDay(day: EventScheduleDay)

        @StateStrategyType(SkipStrategy::class)
        fun scrollContent(day: EventScheduleDay)

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSchemeButton(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showScheme(eventId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onDaySelected(day: EventScheduleDay)
        fun onTagSelected()

        fun onSubEventClick(subEvent: EventActivityModel)
        fun onSchemeClick()
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)

        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
    }
}