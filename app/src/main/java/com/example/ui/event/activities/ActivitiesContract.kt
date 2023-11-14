package com.example.ui.event.activities

import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleDay
import com.example.data.models.Tag
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ActivitiesContract {

    interface View : BaseContract.View {
        @OneExecution
        fun setContentPlaceholder()

        @AddToEndSingle
        fun setDays(days: Map<Int, List<EventScheduleDay>>)

        @AddToEndSingle
        fun setTags(tags: List<Tag>?)

        @AddToEndSingle
        fun setSubEvents(isApproved : Boolean, data: List<SubEventsData>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyEventPlaceholder()

        @OneExecution
        fun selectDay(day: EventScheduleDay)

        @Skip
        fun scrollContent(day: EventScheduleDay)

        @Skip
        fun showSubEvent(eventId: String, subEventId: String)

        @OneExecution
        fun updateSubEvent(subEvent: EventActivityModel)

        @OneExecution
        fun setSchemeButton(show: Boolean)

        @OneExecution
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