package com.example.holders.redesign

import android.view.View
import com.example.R
import com.example.data.models.EventScheduleDay
import com.example.databinding.ItemEventTimetableBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.firstLetterToUppercase
import com.example.extensions.parseAndFormat
import com.example.extensions.parseToLong
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class EventActivityDateItem(
    val data: EventScheduleDay?,
    val id: Long? = null,
) : BindableItem<ItemEventTimetableBinding>(id ?: 0) {

    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
    private val formattedDate = data?.date?.parseAndFormat(defaultServerDateFormatter, dateFormat)
        ?.firstLetterToUppercase()

    private lateinit var binding: ItemEventTimetableBinding
    override fun bind(viewBinding: ItemEventTimetableBinding, position: Int) {
        viewBinding.apply {
            binding = this
            tvTimetableDate.text = formattedDate
        }
    }


    fun getDay(): String? = data?.date
    fun getDate(): EventScheduleDay? = data
    fun getView(): View? {
        return if (this::binding.isInitialized){
            binding.root
        }else null
    }

    override fun getLayout(): Int = R.layout.item_event_timetable

}