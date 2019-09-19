package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.defaultDateTimeFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseAndFormat
import com.example.extensions.substringToWholeWord
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import maxLength

abstract class NotificationItem(
        private val notification: Notification,
        private val onReadMoreClickListener: OnNotificationReadMoreClickListener
) : Item(notification.id.toLong()) {

    abstract fun getTitleView(viewHolder: ViewHolder): TextView
    abstract fun getMessageView(viewHolder: ViewHolder): TextView
    abstract fun getDateView(viewHolder: ViewHolder): TextView
    abstract fun getReadMoreView(viewHolder: ViewHolder): View

    @CallSuper
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            setBackgroundColor(ContextCompat.getColor(context, if (notification.wasRead) R.color.notification_center_notification_read else R.color.notification_center_notification_unread))
        }

        getMessageView(viewHolder).apply {
            isVisible = !notification.message.isNullOrEmpty()
            val message = notification.message?.parseAsHtml()
            val ellipsizedMessage = message?.substringToWholeWord(maxLength)
            text = ellipsizedMessage
            getReadMoreView(viewHolder).isVisible = ellipsizedMessage != message
        }

        getDateView(viewHolder).apply {
            val parsedDate = notification.date.parseAndFormat(defaultServerDateTimeFormatter, defaultDateTimeFormatter)
            text = parsedDate
        }

        getReadMoreView(viewHolder).apply {
            setOnClickListener { onReadMoreClickListener(this@NotificationItem.notification.id) }
        }
    }
}

typealias OnNotificationReadMoreClickListener = (id: Int) -> Unit