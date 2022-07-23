package com.example.ui.event.activities

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.Tag
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ActivitiesContract {

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
        fun setSubEvents(canShow : Boolean, day : EventScheduleCalendarDay, subEvents: Map<String, List<EventActivityModel>>, selectedTags: List<Tag>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollContent(day: EventScheduleCalendarDay)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyEventPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "currentDay")
        fun showCurrentDay(day: EventScheduleCalendarDay, daysSize: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "dataFromCache")
        fun showDataFormCacheMessage(cacheDate: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEventsNew(canShow : Boolean,subEvents: Map<String, List<EventActivityModel>>, selectedTags: List<Tag>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSchemeButton(scheme: List<String>?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onDaySelected(day: EventScheduleCalendarDay)
        fun onTagSelectedListChange()

        fun onSubEventClick(subEvent: EventActivityModel)
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)

        fun onShowAllTagsClick()

        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
    }
}