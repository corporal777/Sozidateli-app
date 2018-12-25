package com.example.util

import android.view.Menu
import com.example.R
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import java.text.SimpleDateFormat
import java.util.*

object Utils {


    val defaultDataFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())


    fun getDatesInterval(startDate: Long, finishDate: Long): String {
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val finish = Calendar.getInstance().apply { timeInMillis = finishDate }

        val startFormat = if (start.get(Calendar.YEAR) == finish.get(Calendar.YEAR)) DATE_FORMAT_SHORT_MONTH_NO_YEAR else DATE_FORMAT_SHORT_MONTH_FULL_YEAR
        val formattedStart = SimpleDateFormat(startFormat, Locale.getDefault()).format(start.time)
        val formattedFinish = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault()).format(finish.time)

        val result = "$formattedStart - $formattedFinish"

        return result
    }


    fun processMainMenu(menu: Menu, onChatClick: () -> Unit, onAccountClick: () -> Unit) {
        val chatItem = menu.findItem(R.id.chat)
        val chatView = chatItem?.actionView as ChatView?
        chatView?.run { setOnClickListener { onChatClick() } }

        val accountItem = menu.findItem(R.id.account)
        val accountView = accountItem?.actionView as AccountView?
        accountView?.run { setOnClickListener { onAccountClick() } }
    }

}