package com.example.holders.redesign

import com.example.R
import com.example.databinding.ItemEventTimetableBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.example.extensions.parseToLong
import com.example.util.firstLetterToUppercase
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class EventActivityDateItem(
    val date: String?,
    private val id: Long? = null,
) : BindableItem<ItemEventTimetableBinding>(id ?: 0) {

    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())

    override fun bind(viewBinding: ItemEventTimetableBinding, position: Int) {
        viewBinding.apply {
            tvTimetableDate.text = date?.parseAndFormat(defaultServerDateFormatter, dateFormat)
                ?.firstLetterToUppercase()
        }
    }


    fun getDay(): String? = date

    override fun getLayout(): Int = R.layout.item_event_timetable

}