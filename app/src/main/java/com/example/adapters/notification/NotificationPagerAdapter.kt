package com.example.adapters.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.adapters.CustomLoadStateAdapter
import com.example.adapters.notification.NotificationVH.OnNotificationActionListener
import com.example.app.R
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.extensions.executePlaceholderLoadState

class NotificationPagerAdapter(private val listener: OnNotificationActionListener) :
    PagingDataAdapter<NotificationLocal, NotificationVH<*>>(AsyncDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH<*> {
        val inflater: (Int) -> View = {
            LayoutInflater.from(parent.context).inflate(it, parent, false)
        }
        return when (viewType) {
            0 -> SimpleNotificationVH(inflater.invoke(R.layout.item_notification_simple), listener)
            1 -> AcceptNotificationVH(inflater.invoke(R.layout.item_notification_accept), listener)
            else -> throw ClassCastException("Error $viewType type of notification item")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.type) {
            Notification.Type.SIMPLE -> 0
            Notification.Type.ACCEPTABLE -> 1
            Notification.Type.RATE -> 2
            else -> -1
        }
    }

    override fun onBindViewHolder(holder: NotificationVH<*>, position: Int) {
        getItem(position)?.let {
            when (holder) {
                is SimpleNotificationVH -> holder.bind(it)
                is AcceptNotificationVH -> holder.bind(it)
            }
        }
    }

    fun updateNotificationWithoutChange(id: Int) {
        val position = snapshot().items.indexOfFirst { x -> x.id == id }
        if (position != -1) notifyItemChanged(position)
    }

    fun updateNotification(notification: NotificationLocal) {
//        for (i in 0 until snapshot().items.size) {
//            var needItem = false
//            snapshot().items[i].let { local ->
//                if (local.id == notification.id) {
//                    local.acceptState = notification.acceptState
//                    local.wasRead = notification.wasRead
//                    notifyItemChanged(i)
//                    needItem = true
//                }
//            }
//            if (needItem) break
//        }
        snapshot().items.find { x -> x.id == notification.id }.let { local ->
            if (local != null) {
                local.acceptState = notification.acceptState
                local.wasRead = notification.wasRead

                val position = snapshot().items.indexOf(local)
                notifyItemChanged(position)
            }
        }
    }

    companion object {

    }

    private object AsyncDiffCallback : DiffUtil.ItemCallback<NotificationLocal>() {
        override fun areItemsTheSame(
            oldItem: NotificationLocal,
            newItem: NotificationLocal
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NotificationLocal,
            newItem: NotificationLocal
        ): Boolean {
            return oldItem == newItem
        }
    }
}