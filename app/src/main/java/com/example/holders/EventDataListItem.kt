package com.example.holders

import com.example.R
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseToDate
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_data_list.*
import java.text.SimpleDateFormat
import java.util.*

class EventDataListItem(
        private val itemId: Long,
        private val name: String?,
        private val address: String?,
        private val conferenceStart: String?
) : Item(itemId) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventAddress.apply {
                text = address
            }
            tvEventLabel.text = name

            val dateStart = conferenceStart?.parseToDate(defaultServerDateFormatter)

            tvEventDay.apply {
                val formatter = SimpleDateFormat("d", Locale("ru", "RU"))
                val day = formatter.format(dateStart)
                text = day
            }

            tvEventDate.apply {
                val formatter = SimpleDateFormat("MMM\n‘yy", Locale("ru", "RU"))
                val formatted = formatter.format(dateStart)
                val result = formatted?.split("\n")?.mapIndexed { index, part ->
                    if (index == 0 && part.length > 3) part.substring(0, 3)
                    else part
                }?.joinToString("\n")
                text = result
            }
        }
    }

    override fun getLayout() = R.layout.item_event_data_list
}