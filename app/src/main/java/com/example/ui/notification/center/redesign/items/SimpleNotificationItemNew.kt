package com.example.ui.notification.center.redesign.items

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Notification
import com.example.databinding.ItemLectureBinding
import com.example.databinding.ItemNotificationSimpleNewBinding
import com.example.holders.OnNotificationReadClickListener
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class SimpleNotificationItemNew(
    private val context: Context,
    private val notification: Notification,
    onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
    private val onReadClickListener: OnNotificationReadClickListener,
    private val openEventListener: OnOpenEventListener
) : NotificationItemNew<ItemNotificationSimpleNewBinding>(
    context,
    notification,
    onLinkClickListener,
    openEventListener
) {

    override fun bind(viewBinding: ItemNotificationSimpleNewBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            btnMarkAsRead.apply {
                if (notification.wasRead) {
                    isEnabled = false
                    text = context.getString(R.string.notifications_was_read)
                } else {
                    isEnabled = true
                    text = context.getString(R.string.notifications_mark_as_read)
                    setOnClickListener { onReadClickListener(notification.id) }
                }
            }
        }
    }


    override fun getBadgeView(viewBinding: ItemNotificationSimpleNewBinding): View =
        viewBinding.viewBadge

    override fun getTitleView(viewBinding: ItemNotificationSimpleNewBinding): TextView =
        viewBinding.tvTitle

    override fun getMessageView(viewBinding: ItemNotificationSimpleNewBinding): TextView =
        viewBinding.tvMessage

    override fun getReadMoreView(viewBinding: ItemNotificationSimpleNewBinding): View =
        viewBinding.tvReadMore

    override fun getLayout() = R.layout.item_notification_simple_new
}

