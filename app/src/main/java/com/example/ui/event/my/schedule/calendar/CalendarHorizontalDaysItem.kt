package com.example.ui.event.my.schedule.calendar

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.isInvisible
import com.example.app.R
import com.example.app.databinding.ItemCalendarHorizontalDaysBinding
import com.example.data.models.EventScheduleDay
import com.example.ui.views.CalendarDayView
import com.xwray.groupie.viewbinding.BindableItem

class CalendarHorizontalDaysItem(
    id: Int?,
    private val listDays: List<EventScheduleDay>,
    private val onDaySelect: (date: EventScheduleDay) -> Unit
) : BindableItem<ItemCalendarHorizontalDaysBinding>(id?.toLong() ?: 0) {

    private lateinit var binding: ItemCalendarHorizontalDaysBinding

    override fun bind(viewBinding: ItemCalendarHorizontalDaysBinding, position: Int) {
        viewBinding.apply {
            binding = this
            daysContainer.updatedViews()
            listDays.forEachIndexed { index, day ->
                val dayView = daysContainer.findView(index)
                if (dayView != null) {
                    dayView.setDay(day)
                    dayView.setOnDaySelected { onDaySelect.invoke(it) }
                }

            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is CalendarHorizontalDaysItem) return false
        if (listDays != other.listDays) return false
        return true
    }

    fun selectDay(day: EventScheduleDay?) {
        if (!this::binding.isInitialized) return
        if (day == null) return
        listDays.forEachIndexed { index, _ ->
            val dayView = binding.daysContainer.findView(index) ?: return@forEachIndexed
            dayView.isDaySelected = dayView.getDay() == day
            dayView.updateView()
        }
    }


    private fun LinearLayout.findView(index: Int): CalendarDayView? {
        val view = this.children.elementAt(index)
        if (view is CalendarDayView) {
            view.isInvisible = false
            return view as CalendarDayView
        } else return null
    }

    private fun LinearLayout.updatedViews(){
        this.children.forEach {
            if (it is CalendarDayView) {
                it.isInvisible = true
                it.isDaySelected = false
            }
        }
    }

    override fun initializeViewBinding(view: View) = ItemCalendarHorizontalDaysBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_calendar_horizontal_days
}