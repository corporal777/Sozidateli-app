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
import com.example.ui.notification.types.projects.items.ProjectsTagsItem.ProjectsInviteType
import com.example.util.weak

class NotificationTagsAdapter : CustomLoadStateAdapter<NotificationTagsAdapter.TagsVH> {

    constructor(typeClick: (type: NotificationType) -> Unit) : super(){
        onTypeClick = typeClick
    }
    constructor(type: Int, typeClick: (type: ProjectsInviteType) -> Unit) : super(){
        onProjectClick = typeClick
        this.type = type
    }

    var canShowContent = false
    private var onTypeClick : (type: NotificationType) -> Unit = {}
    private var onProjectClick : (type: ProjectsInviteType) -> Unit = {}
    private var type = 0

    override fun getViewHolder(view: ViewGroup): TagsVH {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        val inflater = layoutInflater.inflate(R.layout.item_notifications_tags, view, false)
        return if (type == 0) NotesTagsVH(inflater) else ProjectTagsVH(inflater)
    }

    override fun onBindViewHolder(holder: TagsVH, position: Int) = holder.bind()

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.NotLoading && canShowContent
    }

    override fun getItemsCount(): Int = 1

    abstract class TagsVH(itemView: View): RecyclerView.ViewHolder(itemView){
        val viewBinding by viewBinding(ItemNotificationsTagsBinding::bind)
        abstract fun bind()
    }

    inner class NotesTagsVH(itemView: View) : TagsVH(itemView) {
        override fun bind() {
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

    inner class ProjectTagsVH(itemView: View) : TagsVH(itemView) {
        override fun bind() {
            viewBinding.apply {
                root.isVisible = canShowContent

                btnPgrf.isVisible = false
                btnOrganizer.isVisible = false
                btnSystemNotifications.isVisible = false

                btnMyProjects.apply {
                    text = context.getString(R.string.active_invites)
                    setOnClickListener {
                        onProjectClick.invoke(ProjectsInviteType.ACTIVE)
                    }
                }

                btnEvents.apply {
                    text = context.getString(R.string.archive_invites)
                    setOnClickListener {
                        onProjectClick.invoke(ProjectsInviteType.ARCHIVE)
                    }
                }
            }
        }
    }
}