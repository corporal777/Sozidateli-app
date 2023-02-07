package com.example.ui.notification.center.redesign.items

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.ItemNotificationAcceptNewBinding
import com.example.holders.OnNotificationAcceptClickListener
import com.example.holders.OnNotificationReadClickListener
import com.example.ui.views.CtpDialog
import com.example.util.ClickableSpanNew
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class AcceptNotificationItemNew(
    private val context: Context,
    private val notification: Notification,
    onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
    private val acceptClickListener: OnNotificationAcceptClickListener,
    private val openEventListener: OnOpenEventListener
) : NotificationItemNew<ItemNotificationAcceptNewBinding>(
    context,
    notification,
    onLinkClickListener,
    openEventListener
) {

    override fun bind(viewBinding: ItemNotificationAcceptNewBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            when (notification.acceptState) {
                Notification.AcceptState.NONE -> {
                    lnDecline.isVisible = false
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
                        text = getDeclineText(this)
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }
                Notification.AcceptState.CANCELED -> {
                    btnCancel.isVisible = false
                    btnAccept.isVisible = false
                    lnDecline.isVisible = true
                    tvDecline.apply {
                        text = "Приглашение было отклонено"
                    }
                }
            }
        }
    }


    private fun showCancelInfo(context: Context) {
        CtpDialog(context)
            .setSelectCallback {}
    }

    private fun getDeclineText(textView: TextView): SpannableString {
        val clickableSpan = ClickableSpanNew(textView) {
            showCancelInfo(textView.context)
        }
        return SpannableString(textView.context.getString(R.string.decline_text_for_notification)).apply {
            val linkStart = length - 10
            val linkEnd = length - 1
            setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
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

    override fun getLayout() = R.layout.item_notification_accept_new
}
