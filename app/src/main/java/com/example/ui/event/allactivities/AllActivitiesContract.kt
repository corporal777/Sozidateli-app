package com.example.ui.event.allactivities

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.Tag
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface AllActivitiesContract {

    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubEvents(subEvents: List<EventActivityModel>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyEventPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun showEmptyDayPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "placeholder")
        fun hidePlaceholder()

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "dataFromCache")
        fun showDataFormCacheMessage(cacheDate: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "dataFromCache")
        fun hideDataFormCacheMessage()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "currentDay")
        fun showCurrentDay(day: EventScheduleCalendarDay)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubevent(subEvent: EventActivityModel, date: Long)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSubEventsData(subEvents: List<EventActivityModel>, day: List<EventScheduleCalendarDay>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSubEventClick(subEvent: EventActivityModel)
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
    }
}