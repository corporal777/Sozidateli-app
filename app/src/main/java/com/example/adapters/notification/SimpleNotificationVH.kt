package com.example.adapters.notification

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemNotificationSimpleBinding
import com.example.data.models.NotificationLocal
import dev.androidbroadcast.vbpd.viewBinding

class SimpleNotificationVH(val itemView: View, val listener: OnNotificationActionListener) :
    NotificationVH<ItemNotificationSimpleBinding>(itemView, listener) {

    private val viewBinding by viewBinding(ItemNotificationSimpleBinding::bind)

    override fun getTitleView(binding: ItemNotificationSimpleBinding) = binding.tvTitle
    override fun getMessageView(binding: ItemNotificationSimpleBinding) = binding.tvMessage
    override fun getRootView(binding: ItemNotificationSimpleBinding) = binding.clSimpleNotification
    override fun getBadgeView(binding: ItemNotificationSimpleBinding) = binding.viewBadge
    override fun getDateTitleView(binding: ItemNotificationSimpleBinding) = binding.tvDate

    fun bind(notification: NotificationLocal) {
        super.bindViews(viewBinding, notification)
        viewBinding.btnMarkAsRead.apply {
            isEnabled = !notification.wasRead

            setButtonText(
                if (notification.wasRead) context.getString(R.string.notifications_was_read)
                else context.getString(R.string.notifications_mark_as_read)
            )

            showProgressLoading(false)

            setOnClickListener {
                showProgressLoading(true)
                listener.onReadClick(notification.id)
            }
        }
    }
}