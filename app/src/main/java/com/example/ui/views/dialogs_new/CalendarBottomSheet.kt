package com.example.ui.views.dialogs_new


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.BottomSheetCalendarBinding
import com.example.extensions.calendar
import com.example.extensions.dp
import com.example.ui.views.calendarView.CalendarDay
import com.example.ui.views.calendarView.DayViewDecorator
import com.example.ui.views.calendarView.DayViewFacade
import com.example.ui.views.calendarView.spans.DotSpan
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class CalendarBottomSheet(
    private val context: Context,
    private val day: EventScheduleCalendarDay,
    eventDays: List<CalendarDay>,
    val minDate: CalendarDay?,
    val maxDate: CalendarDay?
) {

    private val mBinding = BottomSheetCalendarBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: (date: Calendar) -> Unit = {}
    private var mDialog = BottomSheetDialog(context)

    init {
        mDialog.setContentView(mBinding.root)

        val calendar = day.millis.calendar()
        val date = CalendarDay(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        mBinding.calendarView.apply {
            state().edit()
                .setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .commit()
            tileWidth = 50.dp
            tileHeight = 40.dp
            setTitleMonths(R.array.custom_months)
            setDateSelected(date, true)
            setCurrentDate(date, true)
            addDecorator(EventDecorator(getColor(context, R.color.main_brown_color_new), eventDays))
            addDecorator(SelectedDayDecorator(date))
            setOnDateChangedListener { widget, date, selected ->
                Toast.makeText(context, date.date.toString(), Toast.LENGTH_SHORT).show()
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, date.year)
                cal.set(Calendar.MONTH, date.month - 1)
                cal.set(Calendar.DAY_OF_MONTH, date.day)

                onActionClick(cal)
                mDialog.dismiss()
            }
        }
        mDialog.show()

        mBinding.btnClose.setOnClickListener {
            mDialog.dismiss()
        }
    }

    fun setSelectCallback(block: (date: Calendar) -> Unit): CalendarBottomSheet {
        onActionClick = block
        return this
    }


    class EventDecorator(private val color: Int, dates: Collection<CalendarDay?>?) :
        DayViewDecorator {
        private val dates: HashSet<CalendarDay>
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(7F, color))
        }

        init {
            this.dates = HashSet(dates)
        }
    }

    class SelectedDayDecorator(date: CalendarDay?) :
        DayViewDecorator {
        private val date: CalendarDay
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return date == day
        }


        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(7F, Color.WHITE))
        }

        init {
            this.date = date!!
        }
    }

}