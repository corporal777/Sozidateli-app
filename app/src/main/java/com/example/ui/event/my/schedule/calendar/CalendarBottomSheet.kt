package com.example.ui.event.my.schedule.calendar


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isInvisible
import com.example.App
import com.example.app.R
import com.example.data.models.EventScheduleDay
import com.example.app.databinding.BottomSheetCalendarBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.pagercalendar.calendar.CalendarDay
import com.pagercalendar.calendar.DayViewDecorator
import com.pagercalendar.calendar.DayViewFacade
import com.pagercalendar.calendar.spans.DotSpan
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pagercalendar.calendar.decorators.EventDecorator
import com.pagercalendar.calendar.decorators.SelectedDayDecorator
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Provider


class CalendarBottomSheet(
    context: Context,
    private val day: EventScheduleDay?,
    private val days: List<EventScheduleDay>
) : BottomSheetDialog(context), CalendarBottomSheetContract.View {

    private val mBinding = BottomSheetCalendarBinding.inflate(LayoutInflater.from(context))
    private var onDateClick: (date: EventScheduleDay?) -> Unit = {}
    private val mvpDelegate by lazy { MvpDelegate<CalendarBottomSheet>(this) }


    @InjectPresenter(tag = CALENDAR_TAG)
    lateinit var presenter: CalendarBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<CalendarBottomSheetPresenter>

    @ProvidePresenter(tag = CALENDAR_TAG)
    fun providePresenter(): CalendarBottomSheetPresenter = presenterProvider.get().apply {
        this.selectedDay = day
        this.eventDays = days
    }

    init {
        (context.applicationContext as App).appComponent.inject(this)
        setContentView(mBinding.root)

        mBinding.calendarView.setOnDateChangedListener { widget, date, selected ->
            presenter.onDateSelected(date)
        }
        mBinding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun setCurrentDate(date: CalendarDay?) {
        mBinding.calendarView.apply {
            if (date != null) {
                setTitleMonths(R.array.custom_months)
                setDateSelected(date, true)
                setCurrentDate(date, true)
                addDecorator(SelectedDayDecorator(date))
            }
        }
    }

    override fun setCalendarData(minDate: CalendarDay?, maxDate: CalendarDay?) {
        mBinding.calendarView.apply {
            state().edit()
                .setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .commit()
            isInvisible = false
        }
    }

    override fun setEventDates(dates: List<CalendarDay>) {
        mBinding.calendarView.apply {
            addDecorator(EventDecorator(dates, getColor(context, R.color.main_brown_color_new)))
        }
    }

    override fun setDateSelected(date: EventScheduleDay?) {
        onDateClick(date)
        dismiss()
    }

    fun setDateSelectCallback(block: (date: EventScheduleDay?) -> Unit): CalendarBottomSheet {
        onDateClick = block
        return this
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mvpDelegate.onSaveInstanceState()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }

    companion object {
        private const val CALENDAR_TAG = "calendar_bottom_sheet_tag"
    }


}