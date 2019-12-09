package com.example.holders

import android.content.Context
import com.example.R
import com.example.extensions.calendar
import com.example.extensions.isSameDay
import com.example.extensions.isYesterday
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_chat_date.*
import java.text.SimpleDateFormat
import java.util.*

class ChatDateItem(private val date: Long) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvChatMessageDate.apply {
            text = formatMessageDate(context)
        }
    }

    private fun formatMessageDate(context: Context): String {
        val messageCalendar = date.calendar()
        val now = Calendar.getInstance()

        return when {
            messageCalendar.isSameDay(now) -> context.getString(R.string.today)
            messageCalendar.isYesterday(now) -> context.getString(R.string.yesterday)
            else -> SimpleDateFormat("d · MM · yyyy", Locale.getDefault()).format(date)
        }
    }

    override fun getLayout() = R.layout.item_chat_date
}