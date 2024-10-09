package com.example.ui.notification.items

import com.example.app.R
import com.example.app.databinding.ItemNotificationsDateBinding
import com.example.extensions.dateFormatterFullMothFullYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.xwray.groupie.databinding.BindableItem

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