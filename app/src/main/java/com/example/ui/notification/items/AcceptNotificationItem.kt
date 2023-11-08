package com.example.ui.notification.items

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.ItemNotificationAcceptNewBinding
import com.example.ui.views.CtpDialog
import com.example.util.ClickableSpanNew

class AcceptNotificationItem(
    private val context: Context,
    private val notification: Notification,
    private val listener: OnNotificationActionListener
) : NotificationItem<ItemNotificationAcceptNewBinding>(
    context,
    notification,
    listener
) {

    override fun bind(viewBinding: ItemNotificationAcceptNewBinding, position: Int) {
        super.bind(viewBinding, position)
        decorViews(viewBinding, notification)
    }

    private fun decorViews(
        viewBinding: ItemNotificationAcceptNewBinding,
        notification: Notification
    ) {
        viewBinding.apply {
            when (notification.acceptState) {
                Notification.AcceptState.NONE -> {
                    lnDecline.isVisible = false
                    btnAccept.apply {
                        isVisible = true
                        setOnClickListener { listener.onAcceptClickListener(notification, true) }
                    }
                    btnCancel.apply {
                        isVisible = true
                        setOnClickListener { listener.onAcceptClickListener(notification, false) }
                    }
                }
                Notification.AcceptState.DISABLED -> {
                    lnDecline.apply {
                        isVisible = true
                        tvDecline.text = resources.getString(R.string.notifications_state_disabled)
                    }
                    btnAccept.isVisible = false
                    btnCancel.isVisible = false

                }
                Notification.AcceptState.ACCEPTED -> {
                    btnAccept.isVisible = false
                    btnCancel.isVisible = false
                    lnDecline.isVisible = true
                    tvDecline.apply {
                        highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                        text = getNotificationAcceptedText(this)
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }
                Notification.AcceptState.CANCELED -> {
                    btnCancel.isVisible = false
                    btnAccept.isVisible = false
                    lnDecline.isVisible = true
                    tvDecline.apply {
                        highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                        text = getNotificationDeclinedText(this)
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }
        }
    }

    override fun bind(
        viewBinding: ItemNotificationAcceptNewBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
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

    private fun showCancelInfo(context: Context) {
        CtpDialog(context)
            .setSelectCallback {}
    }

    private fun getNotificationAcceptedText(textView: TextView): SpannableString {
        val clickableSpan = ClickableSpanNew(textView) {
            showCancelInfo(textView.context)
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

    override fun getTitleView(viewBinding: ItemNotificationAcceptNewBinding): TextView =
        viewBinding.tvTitle

    override fun getMessageView(viewBinding: ItemNotificationAcceptNewBinding): TextView =
        viewBinding.tvMessage

    override fun getReadMoreView(viewBinding: ItemNotificationAcceptNewBinding): View =
        viewBinding.tvReadMore

    override fun getBadgeView(viewBinding: ItemNotificationAcceptNewBinding): View =
        viewBinding.viewBadge

    override fun getRootView(viewBinding: ItemNotificationAcceptNewBinding): View =
        viewBinding.clAcceptNotification

    override fun getLayout() = R.layout.item_notification_accept_new
}
