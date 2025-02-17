package com.example.holders

import android.view.View
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.databinding.ItemDayBinding
import com.example.app.databinding.ItemEventTimetableBinding
import com.example.data.models.EventScheduleDay
import com.example.extensions.dateFormatterFullDayFullMonthNoYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.firstLetterToUppercase
import com.example.extensions.parseAndFormat
import com.xwray.groupie.viewbinding.BindableItem
import java.text.SimpleDateFormat
import java.util.Locale


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