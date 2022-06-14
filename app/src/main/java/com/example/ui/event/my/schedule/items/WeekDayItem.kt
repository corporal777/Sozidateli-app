package com.example.ui.event.my.schedule.items

import android.graphics.Color
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemCalendarWeekDayBinding
import com.xwray.groupie.databinding.BindableItem
import java.util.*

class WeekDayItem(
    private val weekDay: String
) : BindableItem<ItemCalendarWeekDayBinding>() {

    override fun bind(viewBinding: ItemCalendarWeekDayBinding, position: Int) {
        viewBinding.apply {
            tvWeekDay.apply {
                if (weekDay.contentEquals("сб") || weekDay.contentEquals("вс")) {
                    setTextColor(Color.parseColor("#543C3C43"))
                }
                text = weekDay
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_calendar_week_day
}