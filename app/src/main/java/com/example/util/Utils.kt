package com.example.util

import android.content.res.Resources
import android.view.Menu
import com.example.R
import com.example.data.models.ProfileField
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    val defaultDateFormatter: DateFormat
        get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())

    val defaultServerDateFormatter: DateFormat
        get() = SimpleDateFormat(DATE_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

    val defaultServerDateFormatterWithTime: DateFormat
        get() = SimpleDateFormat(DATE_TIME_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

    fun formatToDefaultDate(serverTimestamp: String): String? {
        val serverDate = try {
            defaultServerDateFormatter.parse(serverTimestamp)
        } catch (e: ParseException) {
            return null
        }

        return defaultDateFormatter.format(serverDate)
    }

    fun getDatesInterval(startDate: String?, finishDate: String?): String {
        return getDatesInterval(if (startDate == null) 0 else defaultServerDateFormatter.parse(startDate).time, if (finishDate == null) 0 else defaultServerDateFormatter.parse(finishDate).time)
    }

    fun getDatesInterval(startDate: Long, finishDate: Long): String {
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val finish = Calendar.getInstance().apply { timeInMillis = finishDate }

        val startFormat = if (start.get(Calendar.YEAR) == finish.get(Calendar.YEAR)) DATE_FORMAT_SHORT_MONTH_NO_YEAR else DATE_FORMAT_SHORT_MONTH_FULL_YEAR
        val formattedStart = SimpleDateFormat(startFormat, Locale.getDefault()).format(start.time)
        var formattedFinish = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault()).format(finish.time)

        if (finishDate == 0L) formattedFinish = "н.в"

        return "$formattedStart - $formattedFinish"
    }

    fun processMainMenu(menu: Menu, onChatClick: () -> Unit, onAccountClick: () -> Unit) {
        val chatItem = menu.findItem(R.id.chat)
        val chatView = chatItem?.actionView as ChatView?
        chatView?.run { setOnClickListener { onChatClick() } }

        val accountItem = menu.findItem(R.id.account)
        val accountView = accountItem?.actionView as AccountView?
        accountView?.run { setOnClickListener { onAccountClick() } }
    }


    public fun getDataByName(obj: Any?, fieldName: String): Any? {
        if (obj == null) return null
        try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.setAccessible(true)
            val value = field.get(obj)
            return value
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    public fun getListFieldValueByMapDefault(obj: Any?, default: MutableList<ProfileField>): MutableList<ProfileField> {
        val result = mutableListOf<ProfileField>()
        default.forEach { default ->
            result.add(ProfileField(default.type, default.nameField, default.label, default.required, getDataByName(obj, default.nameField)))
        }
        return result
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}