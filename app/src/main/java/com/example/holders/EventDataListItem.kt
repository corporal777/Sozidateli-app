package com.example.holders

import com.example.R
import com.example.extensions.*
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_data_list.*
import java.text.SimpleDateFormat
import java.util.*

class EventDataListItem(
        itemId: Long,
        private val name: String?,
        private val address: String?,
        private val conferenceStart: String?,
        private val conferenceActionStart: String?
) : Item(itemId) {

    companion object {
        private const val ADDRESS_SPLIT_DIVIDER = ","

        private const val SPACE = " "
        private const val SPACE_NO_BREAK = "\u00A0"
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventAddress.apply {
                val startTime = conferenceActionStart?.parseAndFormat(defaultServerDateTimeFormatter, defaultTimeFormatter)
                val textList = listOfNotNull(formatAddress(), startTime)
                text = textList.joinToString("$SPACE_NO_BREAK•$SPACE_NO_BREAK")
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

    private fun formatAddress(): String? {
        return if (address.isNullOrEmpty()) null
        else address.split(ADDRESS_SPLIT_DIVIDER)
                .joinToString { it.trim().replace(SPACE, SPACE_NO_BREAK) }
    }

    override fun getLayout() = R.layout.item_event_data_list
}