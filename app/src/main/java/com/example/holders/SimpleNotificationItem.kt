package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_notification_simple.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class SimpleNotificationItem(
        private val notification: Notification,
        onReadMoreClickListener: OnNotificationReadMoreClickListener,
        onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
        private val onNotificationReadClickListener: OnNotificationReadClickListener,
        private val openEventListener: OnOpenEventListener
) : NotificationItem(notification, onReadMoreClickListener, onLinkClickListener, openEventListener) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            btnMarkAsRead.apply {
                isVisible = !notification.wasRead && !getReadMoreView(viewHolder).isVisible
                setOnClickListener { onNotificationReadClickListener(notification.id) }
            }
        }
    }

    override fun getTitleView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvTitle
    override fun getMessageView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvMessage
    override fun getDateView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvDate
    override fun getReadMoreView(viewHolder: GroupieViewHolder): View = viewHolder.btnReadMore
    override fun getLayout() = R.layout.item_notification_simple
}

typealias OnNotificationReadClickListener = (id: Int) -> Unit