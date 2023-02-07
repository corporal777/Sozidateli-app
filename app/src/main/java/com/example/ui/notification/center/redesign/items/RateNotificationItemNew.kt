package com.example.ui.notification.center.redesign.items

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.ItemNotificationRateNewBinding
import com.example.holders.NotificationItem
import com.example.holders.OnNotificationRateClickListener
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_notification_accept.*
import kotlinx.android.synthetic.main.item_notification_rate.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class RateNotificationItemNew(
    private val context : Context,
    private val notification: Notification,
    onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
    private val rateClickListener: OnNotificationRateClickListener,
    private val openEventListener: OnOpenEventListener
) : NotificationItemNew<ItemNotificationRateNewBinding>(
    context,
    notification,
    onLinkClickListener,
    openEventListener
) {

    override fun bind(viewBinding: ItemNotificationRateNewBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            btnRate.apply {
                isVisible = !notification.wasRead
                setOnClickListener { notification.rateId?.let { rateClickListener(it) } }
            }
        }
    }


    override fun getTitleView(viewBinding: ItemNotificationRateNewBinding): TextView = viewBinding.tvTitle
    override fun getMessageView(viewBinding: ItemNotificationRateNewBinding): TextView = viewBinding.tvMessage
    override fun getReadMoreView(viewBinding: ItemNotificationRateNewBinding): View = viewBinding.tvReadMore
    override fun getBadgeView(viewBinding: ItemNotificationRateNewBinding): View = viewBinding.viewBadge

    override fun getLayout() = R.layout.item_notification_rate_new
}

