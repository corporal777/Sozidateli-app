package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
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

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            btnRate.apply {
                setOnClickListener { notification.rateId?.let { rateClickListener(it) } }
                isVisible = !notification.wasRead
            }
        }
    }

    override fun getTitleView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvTitle
    override fun getMessageView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvMessage
    override fun getDateView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvDate
    override fun getReadMoreView(viewHolder: GroupieViewHolder): View = viewHolder.btnReadMore
    override fun getLayout() = R.layout.item_notification_rate
}

typealias OnNotificationRateClickListener = (rateId: String) -> Unit
