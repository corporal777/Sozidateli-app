package com.example.holders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.ui.views.CtpDialog
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_notification_accept.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class AcceptNotificationItem(
    private val notification: Notification,
    onReadMoreClickListener: OnNotificationReadMoreClickListener,
    onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
    private val acceptClickListener: OnNotificationAcceptClickListener,
    private val changeDecisionClickListener: OnNotificationChangeDecisionClickListener,
    private val openEventListener: OnOpenEventListener,
    private val onNotificationReadClickListener: OnNotificationReadListener
) : NotificationItem(
    notification,
    onReadMoreClickListener,
    onLinkClickListener,
    openEventListener
) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            when (notification.acceptState) {
                Notification.AcceptState.NONE -> {
                    tvAcceptState.isVisible = false
                    btnAccept.apply {
                        isVisible = true
                        setOnClickListener { acceptClickListener(notification, true) }
                    }
                    btnCancel.apply {
                        isVisible = true
                        setOnClickListener { acceptClickListener(notification, false) }
                    }
                }
                Notification.AcceptState.DISABLED -> {
                    tvAcceptState.apply {
                        isVisible = true
                        text = resources.getString(R.string.notifications_state_disabled)
                    }
                    btnAccept.isVisible = false
                    btnCancel.isVisible = false

                }
                Notification.AcceptState.ACCEPTED -> {
                    tvAcceptState.isVisible = false
                    btnAccept.apply {
                        isEnabled = false
                        isVisible = true
                    }
                    btnCancel.apply {
                        isVisible = true
                        setOnClickListener { showCancelInfo(context) }
                    }
                }
                Notification.AcceptState.CANCELED -> {
                    tvAcceptState.isVisible = false
                    btnCancel.apply {
                        isVisible = true
                        isEnabled = false
                    }
                    btnAccept.apply {
                        isVisible = true
                        setOnClickListener { acceptClickListener(notification, true) }
                    }
                }
            }

            btnChangeDecision.apply {
                isVisible = false
                setOnClickListener { changeDecisionClickListener(notification) }
            }

//            tvAcceptState.apply {
//                text = when (notification.acceptState) {
//                    Notification.AcceptState.NONE -> null
//                    Notification.AcceptState.DISABLED -> resources.getString(R.string.notifications_state_disabled)
//                    Notification.AcceptState.ACCEPTED -> resources.getString(R.string.notifications_state_accepted)
//                    Notification.AcceptState.CANCELED -> resources.getString(R.string.notifications_state_cancelled)
//                }
//            }

            val isReadMoreVisible = getReadMoreView(viewHolder).isVisible
            val isAcceptable = notification.acceptState == Notification.AcceptState.NONE
            val isEnabled = notification.acceptState != Notification.AcceptState.DISABLED

//            btnAccept.isVisible = !isReadMoreVisible && isEnabled && isAcceptable
//            btnCancel.isVisible = !isReadMoreVisible && isEnabled && isAcceptable
//            btnChangeDecision.isVisible = !isReadMoreVisible && isEnabled && !isAcceptable
//            tvAcceptState.isVisible = !isReadMoreVisible && !isAcceptable
        }
    }

    private fun showCancelInfo(context: Context) {
        CtpDialog(context)
            .setSelectCallback {}
    }

    override fun getTitleView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvTitle
    override fun getMessageView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvMessage
    override fun getDateView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvDate
    override fun getReadMoreView(viewHolder: GroupieViewHolder): View = viewHolder.btnReadMore
    override fun getLayout() = R.layout.item_notification_accept
}

typealias OnNotificationAcceptClickListener = (notification: Notification, isAccept: Boolean) -> Unit
typealias OnNotificationChangeDecisionClickListener = (notification: Notification) -> Unit
typealias OnNotificationReadListener = (id: Int) -> Unit