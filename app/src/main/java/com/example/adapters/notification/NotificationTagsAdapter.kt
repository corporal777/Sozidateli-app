package com.example.adapters.notification

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapters.AppUpdateAdapter.AppUpdateViewHolder
import com.example.adapters.CustomLoadStateAdapter
import com.example.adapters.SimpleRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.app.R
import com.example.app.databinding.ItemNotificationsTagsBinding
import com.example.app.databinding.ItemUpdateAppBinding
import com.example.data.models.EventActivityModel
import com.example.ui.notification.NotificationType

class NotificationTagsAdapter(val onTypeClick: (type: NotificationType) -> Unit) :
    CustomLoadStateAdapter<NotificationTagsAdapter.NotificationTagsVH>() {

    var canShowContent = false

    override fun getViewHolder(view: ViewGroup): NotificationTagsVH {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        return NotificationTagsVH(
            layoutInflater.inflate(
                R.layout.item_notifications_tags,
                view,
                false
            )
        )
    }

    override fun getItemsCount(): Int = 1

    override fun onBindViewHolder(holder: NotificationTagsVH, position: Int) {
        holder.bind()
    }


    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.NotLoading && canShowContent
    }

    inner class NotificationTagsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemNotificationsTagsBinding::bind)

        fun bind() {
            with(viewBinding) {
                root.isVisible = canShowContent

                btnEvents.setOnClickListener { onTypeClick.invoke(NotificationType.EVENTS) }
                btnMyProjects.setOnClickListener { onTypeClick.invoke(NotificationType.PROJECTS) }
                btnOrganizer.setOnClickListener { onTypeClick.invoke(NotificationType.ORGANIZER) }
                btnPgrf.setOnClickListener { onTypeClick.invoke(NotificationType.ESTIMATES) }
                btnSystemNotifications.setOnClickListener { onTypeClick.invoke(NotificationType.SYSTEM) }
            }
        }


    }
}