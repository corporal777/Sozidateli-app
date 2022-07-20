package com.example.ui.event.my.schedule

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.ui.base.BaseContract
import com.example.ui.event.my.schedule.items.MyScheduleEventsData
import com.example.ui.views.calendarView.CalendarDay
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Group

interface MyScheduleEventsContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setHeaderAndCalendar(
            subEventDays: List<CalendarDay>,
            month: String,
            days: List<EventScheduleCalendarDay>,
            firstDate: CalendarDay?,
            lastDate : CalendarDay?
        )

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContent(data: List<EventNew?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContentNew(data: List<MyScheduleEventsData?>)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(eventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToDay(day: EventScheduleCalendarDay)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectDay(day: EventScheduleCalendarDay)

        @StateStrategyType(SkipStrategy::class)
        fun scrollContent(day: EventScheduleCalendarDay)

        @StateStrategyType(SkipStrategy::class)
        fun showMessageDialog(message: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
        fun onDaySelected(day: EventScheduleCalendarDay)
        fun onShowEventClick(eventId: String)
        fun findNearestEventDay(day: EventScheduleCalendarDay)
        fun onSubEventClick(eventId: String, subEvent: EventActivityModel)
        fun onAddSubEventToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveSubEventFromScheduleClick(subEvent: EventActivityModel)

    }
}