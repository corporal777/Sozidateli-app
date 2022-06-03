package com.example.ui.event.activities

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.Tag
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.custom.LinkedSet
import java.util.*

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
        fun setSubEvents(subEvents: List<EventActivityModel>, selectedTags: List<Tag>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubEventsNew(subEvents: Map<String, LinkedList<EventActivityModel>>, selectedTags: List<Tag>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyEventPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyDayPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun hidePlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "currentDay")
        fun showCurrentDay(day: EventScheduleCalendarDay, daysSize: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "currentDay")
        fun hideCurrentDay()

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "dataFromCache")
        fun showDataFormCacheMessage(cacheDate: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "dataFromCache")
        fun hideDataFormCacheMessage()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAllTags()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubevent(subEvent: EventActivityModel)
    }

    interface Presenter : BaseContract.Presenter {
        fun onDaySelected(day: EventScheduleCalendarDay)
        fun onTagSelectedListChange()
        fun onDayChanged(date: Long)

        fun onSubEventClick(subEvent: EventActivityModel)
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)

        fun onShowAllTagsClick()

        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
    }
}