package com.example.ui.event.my.schedule

import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleData
import com.example.data.models.EventScheduleDay
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface MyScheduleEventsContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setContentPlaceholder()

        @AddToEndSingle
        fun setHeaderCalendar(days: List<List<EventScheduleDay>>)

        @AddToEndSingle
        fun setMonthCalendar(dates: List<EventScheduleDay>, month: String)

        @AddToEndSingle
        fun setContent(data: List<EventScheduleData>)

        @Skip
        fun scrollToDay(day: EventScheduleDay)

        @Skip
        fun scrollContent(day: EventScheduleDay)

        @Skip
        fun selectDay(day: EventScheduleDay)

        @OneExecution
        fun showAboutEvent(eventId: String)

        @Skip
        fun showMessageDialog(message: String)

        @OneExecution
        fun updateSubEvent(subEvent: EventActivityModel)

        @OneExecution
        fun showSubEvent(eventId: String, subEventId: String)

        @OneExecution
        fun getResultForUpdate()

        @Skip
        fun showErrorMessage(eventId: String, message: String)

        @Skip
        fun showLoadingAlertDialog()

        @Skip
        fun hideLoadingAlertDialog()

        @AddToEndSingle
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