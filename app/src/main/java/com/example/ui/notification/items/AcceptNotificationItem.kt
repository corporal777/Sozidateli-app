package com.example.ui.notification.items

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.ItemNotificationAcceptBinding
import com.example.ui.views.CtpDialog
import com.example.ui.views.expandableTextView.CustomExpandableTextView
import com.example.util.ClickableSpanNew

class AcceptNotificationItem(
    private val context: Context,
    private val notification: Notification,
    private val listener: OnNotificationActionListener
) : NotificationItem<ItemNotificationAcceptBinding>(
    context,
    notification,
    listener
) {

    override fun bind(viewBinding: ItemNotificationAcceptBinding, position: Int) {
        super.bind(viewBinding, position)
        decorViews(viewBinding, notification)
    }

    private fun decorViews(viewBinding: ItemNotificationAcceptBinding, notification: Notification) {
        viewBinding.apply {
            tvDecline.apply {
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                movementMethod = LinkMovementMethod.getInstance()
            }
            btnAccept.setOnClickListener { listener.onAcceptClickListener(notification, true) }
            btnCancel.setOnClickListener { listener.onAcceptClickListener(notification, false) }

            when (notification.acceptState) {
                Notification.AcceptState.NONE -> {
                    btnAccept.isVisible = true
                    btnCancel.isVisible = true
                    clActions.background = null
                    tvDecline.isVisible = false
                }
                Notification.AcceptState.DISABLED -> {
                    btnAccept.isVisible = false
                    btnCancel.isVisible = false
                    clActions.background = getDrawable(context, R.drawable.background_notification_decline_view)
                    tvDecline.apply {
                        isVisible = true
                        text = context.getString(R.string.notifications_state_disabled)
                    }
                }
                Notification.AcceptState.ACCEPTED -> {
                    btnAccept.isVisible = false
                    btnCancel.isVisible = false
                    clActions.background = getDrawable(context, R.drawable.background_notification_decline_view)
                    tvDecline.apply {
                        isVisible = true
                        text = getNotificationAcceptedText(tvDecline)
                    }
                }
                Notification.AcceptState.CANCELED -> {
                    btnCancel.isVisible = false
                    btnAccept.isVisible = false
                    clActions.background = getDrawable(context, R.drawable.background_notification_decline_view)
                    tvDecline.apply {
                        isVisible = true
                        text = getNotificationDeclinedText(tvDecline)
                    }
                }
            }
        }
    }

    override fun bind(viewBinding: ItemNotificationAcceptBinding, position: Int, payloads: MutableList<Any>?) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Notification) {
                notification.acceptState = payload.acceptState
                notification.wasRead = payload.wasRead
                decorViews(viewBinding, notification)
                super.bind(viewBinding, position, payloads)
            }
        }
    }


    private fun getNotificationAcceptedText(textView: TextView): SpannableString {
        val clickableSpan = ClickableSpanNew(textView) {
            CtpDialog(context).setSelectCallback {}
        }
        return when (notification.partitionType) {
            //"event" -> SpannableString("Приглашение было принято.")
            "org" -> SpannableString("Приглашение было принято.")
            else -> SpannableString(textView.context.getString(R.string.decline_text_for_notification)).apply {
                val linkStart = length - 10
                val linkEnd = length - 1
                setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }

    private fun getNotificationDeclinedText(textView: TextView): SpannableString {
        val clickableSpan = ClickableSpanNew(textView) {
            listener.onAcceptClickListener(notification, true)
        }
        return when (notification.partitionType) {
            //"event" -> SpannableString("Приглашение было отклонено.")
            "org" -> SpannableString("Приглашение было отклонено.")
            else -> SpannableString(textView.context.getString(R.string.accept_text_for_notification)).apply {
                val linkStart = length - 8
                val linkEnd = length - 1
                setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }

    override fun getTitleView(binding: ItemNotificationAcceptBinding): TextView = binding.tvTitle
    override fun getMessageView(binding: ItemNotificationAcceptBinding): CustomExpandableTextView = binding.tvMessage
    override fun getReadMoreView(binding: ItemNotificationAcceptBinding): View = binding.tvReadMore
    override fun getBadgeView(binding: ItemNotificationAcceptBinding): View = binding.viewBadge
    override fun getRootView(binding: ItemNotificationAcceptBinding): View = binding.lnAcceptNotification

    override fun getLayout() = R.layout.item_notification_accept
}
