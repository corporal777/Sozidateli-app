package com.example.holders

import android.content.Context
import com.example.app.R
import com.example.app.databinding.ItemChatDateBinding
import com.example.extensions.calendar
import com.example.extensions.isSameDay
import com.example.extensions.isYesterday
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class ChatDateItem(private val date: Long) : BindableItem<ItemChatDateBinding>() {

    override fun bind(viewBinding: ItemChatDateBinding, position: Int) {
        viewBinding.tvChatMessageDate.apply {
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