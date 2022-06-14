package com.example.ui.event.my.schedule.items

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemCalendarMonthDayBinding
import com.example.extensions.calendar
import com.xwray.groupie.databinding.BindableItem
import java.util.*

class MonthDayItem(
    val calendar: DateHolder
) : BindableItem<ItemCalendarMonthDayBinding>() {

    override fun bind(viewBinding: ItemCalendarMonthDayBinding, position: Int) {
        viewBinding.apply {

            val cal = calendar.date?.calendar()
            if (cal?.get(Calendar.DAY_OF_WEEK) == 1 || cal?.get(Calendar.DAY_OF_WEEK) == 7) {
                tvDayNumber.setTextColor(ContextCompat.getColor(viewBinding.root.context, R.color.text_color_weekend))
            }else {
                tvDayNumber.setTextColor(ContextCompat.getColor(viewBinding.root.context, R.color.vk_black))
            }
            if (calendar.dayOfMonth != 0) {
                tvDayNumber.text = calendar.dayOfMonth.toString()
            } else {
                tvDayNumber.text = ""
            }

        }
    }

    override fun getLayout(): Int = R.layout.item_calendar_month_day
}