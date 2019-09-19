package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_notification_accept.*

class AcceptNotificationItem(
        private val notification: Notification,
        onReadMoreClickListener: OnNotificationReadMoreClickListener,
        private val acceptClickListener: OnNotificationAcceptClickListener,
        private val changeDecisionClickListener: OnNotificationChangeDecisionClickListener
) : NotificationItem(notification, onReadMoreClickListener) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            btnAccept.apply {
                setOnClickListener { acceptClickListener(notification.id, true) }
            }

            btnCancel.apply {
                setOnClickListener { acceptClickListener(notification.id, false) }
            }

            btnChangeDecision.apply {
                setOnClickListener { changeDecisionClickListener(notification.id) }
            }

            tvAcceptState.apply {
                text = when (notification.acceptState) {
                    Notification.AcceptState.DISABLED,
                    Notification.AcceptState.NONE -> null
                    Notification.AcceptState.ACCEPTED -> resources.getString(R.string.notifications_state_accepted)
                    Notification.AcceptState.CANCELED -> resources.getString(R.string.notifications_state_cancelled)
                }
            }

            val isAcceptable = notification.acceptState == Notification.AcceptState.NONE
            val isEnabled = notification.acceptState != Notification.AcceptState.DISABLED
            btnAccept.isVisible = isEnabled && isAcceptable
            btnCancel.isVisible = isEnabled && isAcceptable
            btnChangeDecision.isVisible = isEnabled && !isAcceptable
            tvAcceptState.isVisible = isEnabled && !isAcceptable
        }
    }

    override fun getTitleView(viewHolder: ViewHolder): TextView = viewHolder.tvTitle
    override fun getMessageView(viewHolder: ViewHolder): TextView = viewHolder.tvMessage
    override fun getDateView(viewHolder: ViewHolder): TextView = viewHolder.tvDate
    override fun getReadMoreView(viewHolder: ViewHolder): View = viewHolder.btnReadMore
    override fun getLayout() = R.layout.item_notification_accept
}

typealias OnNotificationAcceptClickListener = (id: Int, isAccept: Boolean) -> Unit
typealias OnNotificationChangeDecisionClickListener = (id: Int) -> Unit