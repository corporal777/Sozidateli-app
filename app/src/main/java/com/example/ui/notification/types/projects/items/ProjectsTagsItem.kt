package com.example.ui.notification.types.projects.items

import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemNotificationsTagsBinding
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class ProjectsTagsItem(
    val onTypeClick: (type: ProjectsInviteType) -> Unit
) : BindableItem<ItemNotificationsTagsBinding>(-1007L) {


    override fun bind(viewBinding: ItemNotificationsTagsBinding, position: Int) {
        viewBinding.apply {
            secondLn.isVisible = false
            thirdLn.isVisible = false

            btnMyProjects.apply {
                text = context.getString(R.string.active_invites)
                setOnClickListener {
                    onTypeClick.invoke(ProjectsInviteType.ACTIVE)
                }
            }

            btnEvents.apply {
                text = context.getString(R.string.archive_invites)
                setOnClickListener {
                    onTypeClick.invoke(ProjectsInviteType.ARCHIVE)
                }
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is ProjectsTagsItem) return false
        return true
    }

    enum class ProjectsInviteType {
        ACTIVE, ARCHIVE
    }

    override fun getLayout(): Int = R.layout.item_notifications_tags
}