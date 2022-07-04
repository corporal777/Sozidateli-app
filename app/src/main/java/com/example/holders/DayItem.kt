package com.example.holders

import android.graphics.Color
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.extensions.calendar
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_day.*
import kotlinx.android.synthetic.main.item_lecture.*
import java.util.*

open class DayItem(
    val day: EventScheduleCalendarDay,
    private val onDaySelect: (date: EventScheduleCalendarDay) -> Unit
) : Item() {

    var isSelected = false

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            if (!day.hasEvents){
                tvDayName.setTextColor(viewHolder.root.resources.getColorStateList(R.color.input_text_color_disabled))
                tvDayNumber.setTextColor(viewHolder.root.resources.getColorStateList(R.color.text_color_calendar_day_disabled))
            }else {
                tvDayName.setTextColor(viewHolder.root.resources.getColorStateList(R.color.vk_black))
                tvDayNumber.setTextColor(viewHolder.root.resources.getColorStateList(R.color.text_color_calendar_day))
            }

            tvDayName.text = day.dayOfWeek
            tvDayNumber.apply {
                isSelected = this@DayItem.isSelected
                text = day.dayOfMonth.toString()
                setOnClickListener { performSelectClick() }
            }
        }
    }

    private fun performSelectClick() {
        if (isSelected) return
        isSelected = true
        onDaySelect.invoke(day)
        notifyChanged()
    }

    override fun getLayout() = R.layout.item_day
}