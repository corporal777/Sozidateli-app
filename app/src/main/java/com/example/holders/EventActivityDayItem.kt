package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemEventTimetableBinding
import com.example.common.dateFormatterFullDayFullMonthNoYear
import com.example.common.defaultServerDateFormatter
import com.example.extensions.firstLetterToUppercase
import com.example.common.parseAndFormat
import com.xwray.groupie.viewbinding.BindableItem


class EventActivityDayItem(
    val date: String?,
) : BindableItem<ItemEventTimetableBinding>(date.hashCode().toLong()) {

    private val formattedDate =
        date?.parseAndFormat(defaultServerDateFormatter, dateFormatterFullDayFullMonthNoYear)
            ?.firstLetterToUppercase()

    override fun bind(viewBinding: ItemEventTimetableBinding, position: Int) {
        viewBinding.apply {
            tvTimetableDate.text = formattedDate
        }
    }


    override fun initializeViewBinding(view: View) = ItemEventTimetableBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_timetable

}