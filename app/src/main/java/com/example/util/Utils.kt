package com.example.util

import android.view.Menu
import com.example.R
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import java.text.SimpleDateFormat
import java.util.*

object Utils {


    val defaultDataFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    val defaultServerDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getDatesInterval(startDate: String?, finishDate: String?): String {
        return getDatesInterval(if (startDate == null) 0 else defaultServerDateFormatter.parse(startDate).time, if (finishDate == null) 0 else defaultServerDateFormatter.parse(finishDate).time)
    }


    fun getDatesInterval(startDate: Long, finishDate: Long): String {
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val finish = Calendar.getInstance().apply { timeInMillis = finishDate }

        val startFormat = if (start.get(Calendar.YEAR) == finish.get(Calendar.YEAR)) DATE_FORMAT_SHORT_MONTH_NO_YEAR else DATE_FORMAT_SHORT_MONTH_FULL_YEAR
        val formattedStart = SimpleDateFormat(startFormat, Locale.getDefault()).format(start.time)
        var formattedFinish = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault()).format(finish.time)

        if(finishDate==0L) formattedFinish = "н.в"

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