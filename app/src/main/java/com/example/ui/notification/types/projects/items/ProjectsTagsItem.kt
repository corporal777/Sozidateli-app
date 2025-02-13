package com.example.ui.notification.types.projects.items

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemNotificationsTagsBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is ProjectsTagsItem) return false
        return true
    }

    enum class ProjectsInviteType {
        ACTIVE, ARCHIVE
    }

    override fun initializeViewBinding(view: View) = ItemNotificationsTagsBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_notifications_tags
}