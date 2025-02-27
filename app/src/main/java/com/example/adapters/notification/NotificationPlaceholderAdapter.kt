package com.example.adapters.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.adapters.CustomLoadStateAdapter
import com.example.app.R
import com.example.app.databinding.ItemNotificationPlaceholderBinding
import com.example.app.databinding.ItemNotificationsListPlaceholderBinding
import dev.androidbroadcast.vbpd.viewBinding

class NotificationPlaceholderAdapter(private val isHeader: Boolean, val count: Int = 1) :
    CustomLoadStateAdapter<com.example.adapters.ViewHolder>() {


    override fun getViewHolder(view: ViewGroup): com.example.adapters.ViewHolder {
        val inflater: (Int) -> View = {
            LayoutInflater.from(view.context).inflate(it, view, false)
        }
        return if (isHeader)
            NotificationHeaderPVH(inflater.invoke(R.layout.item_notification_placeholder))
        else NotificationFooterPVH(inflater.invoke(R.layout.item_notifications_list_placeholder))

    }

    override fun getItemsCount(): Int = count


    override fun onBindViewHolder(holder: com.example.adapters.ViewHolder, position: Int) {}

    inner class NotificationHeaderPVH(itemView: View) : com.example.adapters.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemNotificationPlaceholderBinding::bind)
    }

    inner class NotificationFooterPVH(itemView: View) : com.example.adapters.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemNotificationsListPlaceholderBinding::bind)
    }
}