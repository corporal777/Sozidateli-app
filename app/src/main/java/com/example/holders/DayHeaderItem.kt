package com.example.holders

import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_day_header.*
import java.text.SimpleDateFormat
import java.util.*

open class DayHeaderItem(
        val date: EventScheduleCalendarDay
) : Item(date.millis) {

    private val dateFormat = SimpleDateFormat("EE dd.MM", Locale.getDefault())

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvDate.text = dateFormat.format(date.millis)
        }
    }

    override fun getLayout() = R.layout.item_day_header
}