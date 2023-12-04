package com.example.ui.event.my.schedule.calendar

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.core.view.isInvisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleDay
import com.example.databinding.ItemCalendarHorizontalDaysBinding
import com.example.ui.views.CalendarDayView
import com.xwray.groupie.databinding.BindableItem

class CalendarHorizontalDaysItem(
    id: Int?,
    val listDays: List<EventScheduleDay>,
    val onDaySelect: (date: EventScheduleDay) -> Unit
) : BindableItem<ItemCalendarHorizontalDaysBinding>(id?.toLong() ?: 0) {

    private lateinit var binding: ItemCalendarHorizontalDaysBinding

    override fun bind(viewBinding: ItemCalendarHorizontalDaysBinding, position: Int) {
        viewBinding.apply {
            binding = this
            listDays.forEachIndexed { index, day ->
                val dayView = daysContainer.findView(index)
                if (dayView != null) {
                    dayView.setDay(day)
                    dayView.updateView()
                    dayView.setOnDaySelected {
                        onDaySelect.invoke(it)
                    }
                }

            }
        }
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

    fun isHasDay(day: EventScheduleDay?): Boolean {
        if (!this::binding.isInitialized) return false
        val index = listDays.indexOf(day)
        return if (index >= 0){
            val view = binding.daysContainer.findView(index)
            return view != null && view.getDay() == day
        } else false
    }

    private fun LinearLayout.findView(index: Int): CalendarDayView? {
        val view = this.children.elementAt(index)
        if (view is CalendarDayView) {
            view.isInvisible = false
            return view as CalendarDayView
        } else return null
    }
    override fun getLayout(): Int = R.layout.item_calendar_horizontal_days
}