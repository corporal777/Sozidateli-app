package com.example.ui.notification.center.redesign.items

import com.example.R
import com.example.databinding.ItemEventTimetableBinding
import com.example.databinding.ItemNotificationsDateBinding
import com.example.extensions.dateFormatterFullMothFullYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.example.util.firstLetterToUppercase
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class NotificationsDateItem(
    val date: String?,
    private val id: Long? = null,
) : BindableItem<ItemNotificationsDateBinding>(id ?: 0) {

    override fun bind(viewBinding: ItemNotificationsDateBinding, position: Int) {
        viewBinding.apply {
            tvDate.text =
                date?.parseAndFormat(defaultServerDateFormatter, dateFormatterFullMothFullYear)
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is NotificationsDateItem) return false
        if (date != other.date) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_notifications_date

}