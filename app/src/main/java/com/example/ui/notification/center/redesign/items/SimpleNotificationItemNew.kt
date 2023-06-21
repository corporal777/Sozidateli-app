package com.example.ui.notification.center.redesign.items

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Notification
import com.example.databinding.ItemLectureBinding
import com.example.databinding.ItemNotificationSimpleNewBinding
import com.example.holders.OnNotificationReadClickListener
import com.example.holders.OnOpenEventListener
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class SimpleNotificationItemNew(
    private val context: Context,
    private val notification: Notification,
    private val listener: OnNotificationActionListener
) : NotificationItemNew<ItemNotificationSimpleNewBinding>(
    context,
    notification,
    listener
) {

    override fun bind(viewBinding: ItemNotificationSimpleNewBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.btnMarkAsRead.apply {
            decorViews(this, notification)
            setOnClickListener {
                listener.onReadClickListener(notification.id)
            }
        }
    }


    override fun bind(
        viewBinding: ItemNotificationSimpleNewBinding,
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


    override fun getBadgeView(viewBinding: ItemNotificationSimpleNewBinding): View =
        viewBinding.viewBadge

    override fun getTitleView(viewBinding: ItemNotificationSimpleNewBinding): TextView =
        viewBinding.tvTitle

    override fun getMessageView(viewBinding: ItemNotificationSimpleNewBinding): TextView =
        viewBinding.tvMessage

    override fun getReadMoreView(viewBinding: ItemNotificationSimpleNewBinding): View =
        viewBinding.tvReadMore

    override fun getRootView(viewBinding: ItemNotificationSimpleNewBinding): View =
        viewBinding.clSimpleNotification




    override fun getLayout() = R.layout.item_notification_simple_new
}

