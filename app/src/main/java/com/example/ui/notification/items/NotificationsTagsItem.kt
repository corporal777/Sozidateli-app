package com.example.ui.notification.items

import com.example.R
import com.example.databinding.ItemNotificationsTagsBinding
import com.example.ui.notification.NotificationType
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class NotificationsTagsItem(
    val onTypeClick: (type: NotificationType) -> Unit
) : BindableItem<ItemNotificationsTagsBinding>(-1001L) {


    override fun bind(viewBinding: ItemNotificationsTagsBinding, position: Int) {
        viewBinding.apply {
            btnAllNotifications.setOnClickListener {

            }
            btnEvents.setOnClickListener {
                onTypeClick.invoke(NotificationType.EVENTS)
            }
            btnMyProjects.setOnClickListener {
                onTypeClick.invoke(NotificationType.PROJECTS)
            }
            btnOrganizer.setOnClickListener {
                onTypeClick.invoke(NotificationType.ORGANIZER)
            }
            btnPgrf.setOnClickListener {
                onTypeClick.invoke(NotificationType.ESTIMATES)
            }
            btnSystemNotifications.setOnClickListener {
                onTypeClick.invoke(NotificationType.SYSTEM)
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is NotificationsTagsItem) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_notifications_tags
}