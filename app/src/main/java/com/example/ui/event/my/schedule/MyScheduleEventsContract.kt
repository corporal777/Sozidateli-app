package com.example.ui.event.my.schedule

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleDay
import com.example.ui.base.BaseContract
import com.example.data.models.EventScheduleData
import com.example.util.AddToEndSingleByTagStateStrategy

interface MyScheduleEventsContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setContentPlaceholder()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setHeaderCalendar(days: List<List<EventScheduleDay>>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setMonthCalendar(
            dates: List<EventScheduleDay>,
            month : String
        )

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setContent(data: List<EventScheduleData>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToDay(day: EventScheduleDay)

        @StateStrategyType(SkipStrategy::class)
        fun scrollContent(day: EventScheduleDay)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectDay(day: EventScheduleDay)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(eventId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showMessageDialog(message: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSubEvent(subEvent: EventActivityModel)

        @StateStrategyType(SkipStrategy::class)
        fun showSubEvent(eventId: String, subEventId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun getResultForUpdate()

        @StateStrategyType(SkipStrategy::class)
        fun showErrorMessage(eventId: String, message: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLoadingAlertDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideLoadingAlertDialog()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showEmptyListPlaceholder()
    }

    interface Presenter : BaseContract.Presenter {
        fun onSearchTextChange(text: String)
        fun onSearchTextSubmit(text: String)
        fun onDaySelected(day: EventScheduleDay)
        fun onShowEventClick(eventId: String)
        fun onSubEventClick(eventId: String, subEvent: EventActivityModel)
        fun onAddSubEventToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveSubEventFromScheduleClick(subEvent: EventActivityModel)
    }
}