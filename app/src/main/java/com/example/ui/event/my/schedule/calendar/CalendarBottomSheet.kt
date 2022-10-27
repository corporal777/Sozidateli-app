package com.example.ui.event.my.schedule.calendar


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.core.content.ContextCompat.getColor
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.App
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.BottomSheetCalendarBinding
import com.example.extensions.dp
import com.example.ui.views.calendarView.CalendarDay
import com.example.ui.views.calendarView.DayViewDecorator
import com.example.ui.views.calendarView.DayViewFacade
import com.example.ui.views.calendarView.spans.DotSpan
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import javax.inject.Inject
import javax.inject.Provider


class CalendarBottomSheet(
    context: Context,
    private val day: EventScheduleCalendarDay,
    private val days: List<EventActivityModel>
) : BottomSheetDialog(context), CalendarBottomSheetContract.View {

    private val mBinding = BottomSheetCalendarBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: (date: Calendar) -> Unit = {}
    private val mvpDelegate by lazy { MvpDelegate<CalendarBottomSheet>(this) }


    @InjectPresenter(type = PresenterType.WEAK, tag = CALENDAR_TAG)
    lateinit var presenter: CalendarBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<CalendarBottomSheetPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CALENDAR_TAG)
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
        }
    }

    override fun setEventDates(dates: List<CalendarDay>) {
        mBinding.calendarView.apply {
            addDecorator(EventDecorator(getColor(context, R.color.main_brown_color_new), dates))
        }
    }

    override fun setDateSelected(cal: Calendar) {
        onActionClick(cal)
        dismiss()
    }

    fun setSelectCallback(block: (date: Calendar) -> Unit): CalendarBottomSheet {
        onActionClick = block
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

    companion object {
        private const val CALENDAR_TAG = "calendar_bottom_sheet_tag"
    }


}