package com.example.ui.notification.center.redesign.items

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
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
        viewBinding.apply {
            this.root.doOnLayout {
                if (!notification.wasRead && !getReadMoreView(viewBinding).isVisible) {
                    listener.onReadListener(notification.id)
                }
            }

            btnMarkAsRead.apply {
                if (notification.wasRead) {
                    isEnabled = false
                    text = context.getString(R.string.notifications_was_read)
                } else {
                    isEnabled = true
                    text = context.getString(R.string.notifications_mark_as_read)
                    setOnClickListener {
                        listener.onReadClickListener(notification.id)
                    }
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

