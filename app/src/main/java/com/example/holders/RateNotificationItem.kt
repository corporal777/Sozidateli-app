package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_notification_accept.btnReadMore
import kotlinx.android.synthetic.main.item_notification_accept.tvDate
import kotlinx.android.synthetic.main.item_notification_accept.tvMessage
import kotlinx.android.synthetic.main.item_notification_accept.tvTitle
import kotlinx.android.synthetic.main.item_notification_rate.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class RateNotificationItem(
        private val notification: Notification,
        onReadMoreClickListener: OnNotificationReadMoreClickListener,
        onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
        private val rateClickListener: OnNotificationRateClickListener
) : NotificationItem(notification, onReadMoreClickListener, onLinkClickListener) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            btnRate.apply {
                setOnClickListener { rateClickListener(notification.id) }
                isVisible = !notification.wasRead
            }
        }
    }

    override fun getTitleView(viewHolder: ViewHolder): TextView = viewHolder.tvTitle
    override fun getMessageView(viewHolder: ViewHolder): TextView = viewHolder.tvMessage
    override fun getDateView(viewHolder: ViewHolder): TextView = viewHolder.tvDate
    override fun getReadMoreView(viewHolder: ViewHolder): View = viewHolder.btnReadMore
    override fun getLayout() = R.layout.item_notification_rate
}

typealias OnNotificationRateClickListener = (id: Int) -> Unit
