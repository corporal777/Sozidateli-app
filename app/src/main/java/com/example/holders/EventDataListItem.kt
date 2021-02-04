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

    var showStartTime = true

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventAddress.apply {
                if (!address.isNullOrEmpty()) {
                    val textList = listOfNotNull(formatAddress())
                    text = textList.joinToString()
                }
            }
            tvEventLabel.text = name

            val dateStart = conferenceStart?.parseToDate(defaultServerDateFormatter)


            tvEventDay.apply {
                text = dateStart?.let { SimpleDateFormat("d", Locale("ru", "RU")).format(it) }
            }

            tvEventDate.apply {
                text = dateStart
                        ?.let { SimpleDateFormat("MMM\n‘yy", Locale("ru", "RU")).format(it) }
                        ?.split("\n")?.mapIndexed { index, part ->
                            if (index == 0 && part.length > 3) part.substring(0, 3)
                            else part
                        }
                        ?.joinToString("\n")
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