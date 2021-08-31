package com.example.holders

import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_day_header.*
import java.text.SimpleDateFormat
import java.util.*

open class DayHeaderItem(
        val date: Long,
        val isDay: Boolean = true
) : Item(date) {

    private val dateFormat = SimpleDateFormat("EE d.MM.yyyy", Locale.getDefault())
    private val dateFormatWithoutDay = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            if (isDay) tvDate.text = dateFormat.format(date).capitalize()
            else tvDate.text = dateFormatWithoutDay.format(date).capitalize()
        }
    }

    override fun getLayout() = R.layout.item_day_header
}