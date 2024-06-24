package com.example.ui.notification.items

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.ItemNotificationRateBinding
import com.example.ui.views.expandableTextView.CustomExpandableTextView
import com.example.ui.views.expandableTextView.ExpandableTextView

class RateNotificationItem(
    private val context: Context,
    private val notification: Notification,
    private val listener: OnNotificationActionListener
) : NotificationItem<ItemNotificationRateBinding>(
    context,
    notification,
    listener
) {

    override fun bind(viewBinding: ItemNotificationRateBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            btnRate.apply {
                isVisible = !notification.wasRead
                setOnClickListener {
                    notification.rateId?.let {
                        listener.onRateClickListener(it)
                    }
                }
            }
        }
    }


    override fun bind(
        viewBinding: ItemNotificationRateBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Notification) {
                notification.wasRead = payload.wasRead
                viewBinding.btnRate.isVisible = !notification.wasRead
                super.bind(viewBinding, position, payloads)
            }
        }
    }

    override fun getTitleView(binding: ItemNotificationRateBinding): TextView = binding.tvTitle
    override fun getMessageView(binding: ItemNotificationRateBinding): CustomExpandableTextView = binding.tvMessage
    override fun getBadgeView(binding: ItemNotificationRateBinding): View = binding.viewBadge
    override fun getRootView(binding: ItemNotificationRateBinding): View = binding.clRateNotification

    override fun getLayout() = R.layout.item_notification_rate
}

