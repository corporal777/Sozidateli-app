package com.example.ui.notification.items

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.ItemNotificationSimpleBinding
import com.example.ui.views.expandableTextView.CustomExpandableTextView
import com.example.ui.views.expandableTextView.ExpandableTextView

class SimpleNotificationItem(
    private val context: Context,
    private val notification: Notification,
    private val listener: OnNotificationActionListener
) : NotificationItem<ItemNotificationSimpleBinding>(
    context,
    notification,
    listener
) {

    override fun bind(viewBinding: ItemNotificationSimpleBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.btnMarkAsRead.apply {
            decorViews(this, notification)
            setOnClickListener {
                listener.onReadClickListener(notification.id)
            }
        }
    }


    override fun bind(
        viewBinding: ItemNotificationSimpleBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Notification) {
                notification.wasRead = payload.wasRead
                decorViews(viewBinding.btnMarkAsRead, payload)
                super.bind(viewBinding, position, payloads)
            }
        }
    }

    private fun decorViews(button: Button, notification: Notification) {
        button.apply {
            isEnabled = !notification.wasRead
            text = if (notification.wasRead) context.getString(R.string.notifications_was_read)
            else context.getString(R.string.notifications_mark_as_read)
        }
    }


    override fun getBadgeView(binding: ItemNotificationSimpleBinding): View = binding.viewBadge
    override fun getTitleView(binding: ItemNotificationSimpleBinding): TextView = binding.tvTitle
    override fun getMessageView(binding: ItemNotificationSimpleBinding): CustomExpandableTextView = binding.tvMessage
    override fun getRootView(binding: ItemNotificationSimpleBinding): View = binding.clSimpleNotification

    override fun getLayout() = R.layout.item_notification_simple
}

