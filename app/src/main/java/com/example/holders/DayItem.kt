package com.example.holders

import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_day.*

open class DayItem(
        val day: EventScheduleCalendarDay,
        private val onDaySelect: (date: EventScheduleCalendarDay) -> Unit
) : Item() {

    var isSelected = false

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
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