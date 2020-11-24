package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_notification_accept.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class AcceptNotificationItem(
        private val notification: Notification,
        onReadMoreClickListener: OnNotificationReadMoreClickListener,
        onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
        private val acceptClickListener: OnNotificationAcceptClickListener,
        private val changeDecisionClickListener: OnNotificationChangeDecisionClickListener
) : NotificationItem(notification, onReadMoreClickListener, onLinkClickListener) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
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
                    Notification.AcceptState.NONE -> null
                    Notification.AcceptState.DISABLED -> resources.getString(R.string.notifications_state_disabled)
                    Notification.AcceptState.ACCEPTED -> resources.getString(R.string.notifications_state_accepted)
                    Notification.AcceptState.CANCELED -> resources.getString(R.string.notifications_state_cancelled)
                }
            }

            val isReadMoreVisible = getReadMoreView(viewHolder).isVisible
            val isAcceptable = notification.acceptState == Notification.AcceptState.NONE
            val isEnabled = notification.acceptState != Notification.AcceptState.DISABLED
            /*if (notification.acceptState == Notification.AcceptState.CANCELED) {
                btnAccept.isVisible = false
                btnCancel.isVisible = false
            } else {
                btnAccept.isVisible = !isReadMoreVisible && isEnabled && isAcceptable
                btnCancel.isVisible = !isReadMoreVisible && isEnabled && isAcceptable
            }*/
            btnAccept.isVisible = !isReadMoreVisible && isEnabled && isAcceptable
            btnCancel.isVisible = !isReadMoreVisible && isEnabled && isAcceptable
            btnChangeDecision.isVisible = !isReadMoreVisible && isEnabled && !isAcceptable
            tvAcceptState.isVisible = !isReadMoreVisible && !isAcceptable
        }
    }

    override fun getTitleView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvTitle
    override fun getMessageView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvMessage
    override fun getDateView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvDate
    override fun getReadMoreView(viewHolder: GroupieViewHolder): View = viewHolder.btnReadMore
    override fun getLayout() = R.layout.item_notification_accept
}

typealias OnNotificationAcceptClickListener = (id: Int, isAccept: Boolean) -> Unit
typealias OnNotificationChangeDecisionClickListener = (id: Int) -> Unit