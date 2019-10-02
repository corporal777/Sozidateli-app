package com.example.holders

import android.text.util.Linkify
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
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import maxLength
import me.saket.bettermovementmethod.BetterLinkMovementMethod

abstract class NotificationItem(
        private val notification: Notification,
        private val onReadMoreClickListener: OnNotificationReadMoreClickListener,
        private val onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener
) : Item(notification.id.toLong()) {

    abstract fun getTitleView(viewHolder: GroupieViewHolder): TextView
    abstract fun getMessageView(viewHolder: GroupieViewHolder): TextView
    abstract fun getDateView(viewHolder: GroupieViewHolder): TextView
    abstract fun getReadMoreView(viewHolder: GroupieViewHolder): View

    @CallSuper
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            setBackgroundColor(ContextCompat.getColor(context, if (notification.wasRead) R.color.notification_center_notification_read else R.color.notification_center_notification_unread))
        }

        getMessageView(viewHolder).apply {
            isVisible = !notification.message.isNullOrEmpty()
            val message = notification.message?.parseAsHtml()
            val ellipsizedMessage = message?.substringToWholeWord(maxLength)
            text = ellipsizedMessage
            getReadMoreView(viewHolder).isVisible = ellipsizedMessage != message
            BetterLinkMovementMethod.linkify(Linkify.ALL, this)
                    .setOnLinkClickListener(onLinkClickListener)
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