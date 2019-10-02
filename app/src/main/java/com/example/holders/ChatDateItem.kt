package com.example.holders

import com.example.R
import com.example.extensions.*
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat_date.*
import java.util.*

class ChatDateItem(private val date: Long) : Item() {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.tvChatMessageDate.text = formatMessageDate()
    }

    private fun formatMessageDate(): String {
        val messageCalendar = date.calendar()
        val now = Calendar.getInstance()
        return when {
            now.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR) -> dateFormatterFullMothNoYear.format(date)
            else -> dateFormatterShortMoth.format(date)
        }
    }

    override fun getLayout() = R.layout.item_chat_date
}