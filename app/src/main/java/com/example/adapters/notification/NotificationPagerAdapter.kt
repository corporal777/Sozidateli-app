package com.example.adapters.notification

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.adapters.CustomLoadStateAdapter
import com.example.adapters.EventPagingAdapter
import com.example.adapters.notification.NotificationVH.OnNotificationActionListener
import com.example.app.R
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.exceptions.EmptyDataException

class NotificationPagerAdapter(private val listener: OnNotificationActionListener) :
    PagingDataAdapter<NotificationLocal, NotificationVH<*>>(AsyncDiffCallback) {

    private var isFirstLaunch = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH<*> {
        val inflater : (Int) -> View = {
            LayoutInflater.from(parent.context).inflate(it, parent, false)
        }
        return when (viewType) {
            0 -> SimpleNotificationVH(inflater.invoke(R.layout.item_notification_simple), listener)
            1 -> AcceptNotificationVH(inflater.invoke(R.layout.item_notification_accept),listener)
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

    fun updateUserNotificationWithoutChange(id : Int){
        val position = snapshot().items.indexOfFirst { x -> x.id == id }
        if (position != -1) notifyItemChanged(position)
    }

    fun updateUserNotification(notification: NotificationLocal) {
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
        fun NotificationPagerAdapter.withLoadStateAdapters(
            tagsAdapter: NotificationTagsAdapter,
            header: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>,
            onEmpty: (show: Boolean) -> Unit
        ): ConcatAdapter {
            addOnPagesUpdatedListener {}
            addLoadStateListener { loadState ->
                if (itemCount > 0){
                    header.loadState = header.notRefresh
                } else header.loadState = loadState.refresh

                footer.loadState = loadState.append

                tagsAdapter.loadState = header.loadState
                tagsAdapter.canShowContent = true


                if (loadState.refresh is LoadState.Error)
                    if ((loadState.refresh as LoadState.Error).error is EmptyDataException)
                        if (this.snapshot().isEmpty()) onEmpty.invoke(true)
                        else onEmpty.invoke(false)
                    else onEmpty.invoke(false)
                else onEmpty.invoke(false)
            }
            return ConcatAdapter(tagsAdapter, header, this, footer)
        }
    }

    private object AsyncDiffCallback : DiffUtil.ItemCallback<NotificationLocal>() {
        override fun areItemsTheSame(oldItem: NotificationLocal, newItem: NotificationLocal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationLocal, newItem: NotificationLocal): Boolean {
            return oldItem == newItem
        }
    }
}