package com.example.adapters.notification

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemNotificationAcceptBinding
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.ClickableSpanNew
import com.example.util.getDrawable
import dev.androidbroadcast.vbpd.viewBinding

class AcceptNotificationVH(val itemView: View, val listener: OnNotificationActionListener) :
    NotificationVH<ItemNotificationAcceptBinding>(itemView, listener) {

    private val viewBinding by viewBinding(ItemNotificationAcceptBinding::bind)

    override fun getTitleView(binding: ItemNotificationAcceptBinding) = binding.tvTitle
    override fun getMessageView(binding: ItemNotificationAcceptBinding) = binding.tvMessage
    override fun getRootView(binding: ItemNotificationAcceptBinding) = binding.lnAcceptNotification
    override fun getBadgeView(binding: ItemNotificationAcceptBinding) = binding.viewBadge
    override fun getDateTitleView(binding: ItemNotificationAcceptBinding) = binding.tvDate

    fun bind(notification: NotificationLocal) {
        super.bindViews(viewBinding, notification)

        viewBinding.apply {
            tvDecline.apply {
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                movementMethod = LinkMovementMethod.getInstance()
            }
            btnAccept.apply {
                showProgressLoading(false)
                setOnClickListener {
                    showProgressLoading(true)
                    listener.onAcceptClick(notification, true)
                }
            }
            btnCancel.apply {
                showProgressLoading(false)
                setOnClickListener {
                    showProgressLoading(true)
                    listener.onAcceptClick(notification, false)
                }
            }

            when (notification.acceptState) {
                Notification.AcceptState.NONE -> {
                    groupButtons.isVisible = true
                    viewActions.background = null
                    tvDecline.isVisible = false
                }

                Notification.AcceptState.DISABLED -> {
                    groupButtons.isVisible = false
                    viewActions.background = root.getDrawable(R.drawable.background_notification_declined)
                    tvDecline.apply {
                        isVisible = true
                        text = context.getString(R.string.notifications_state_disabled)
                    }
                }

                Notification.AcceptState.ACCEPTED -> {
                    groupButtons.isVisible = false
                    viewActions.background = root.getDrawable(R.drawable.background_notification_declined)
                    tvDecline.apply {
                        isVisible = true
                        text = getNotificationAcceptedText(notification)
                    }
                }

                Notification.AcceptState.CANCELED -> {
                    groupButtons.isVisible = false
                    viewActions.background = root.getDrawable(R.drawable.background_notification_declined)
                    tvDecline.apply {
                        isVisible = true
                        text = getNotificationDeclinedText(notification)
                    }
                }
            }
        }
    }

    private fun TextView.getNotificationAcceptedText(notification: NotificationLocal): SpannableString {
        val clickableSpan = ClickableSpanNew(this) {
            val supportEmail = context.getString(R.string.support_email)
            val message = context.getString(R.string.ctp_text).format(supportEmail).toSpannable()
            Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
            DefaultAlertDialog(
                itemView.context,
                itemView.context.getString(R.string.dear_user_text),
                message
            )
        }
        return when (notification.partitionType) {
            //"event" -> SpannableString("Приглашение было принято.")
            "org" -> SpannableString("Приглашение было принято.")
            else -> SpannableString(context.getString(R.string.decline_text_for_notification)).apply {
                val linkStart = length - 10
                val linkEnd = length - 1
                setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }

    private fun TextView.getNotificationDeclinedText(notification: NotificationLocal): SpannableString {
        val clickableSpan = ClickableSpanNew(this) {
            listener.onAcceptClick(notification, true)
        }
        return when (notification.partitionType) {
            //"event" -> SpannableString("Приглашение было отклонено.")
            "org" -> SpannableString("Приглашение было отклонено.")
            else -> SpannableString(context.getString(R.string.accept_text_for_notification)).apply {
                val linkStart = length - 8
                val linkEnd = length - 1
                setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }
}