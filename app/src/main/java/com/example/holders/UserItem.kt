package com.example.holders

import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_user.*
import setCircleImage

class UserItem(
        private val id: Int,
        private val name: String,
        private val description: String?,
        private val avatar: String?,
        private val onUserClick: () -> Unit,
        var action: UserSubscribeButton.Action? = null,
        private val onActionClick: (() -> Unit)? = null
) : Item(id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = name
            tvDescription.apply {
                isVisible = !description.isNullOrEmpty()
                text = description
            }
            ivUserAvatar.setCircleImage(avatar, R.drawable.avatar_placeholder)
            itemView.setOnClickListener { onUserClick.invoke() }
            btnAction.apply {
                val action = this@UserItem.action
                visibility = if (action != null) {
                    setAction(action)
                    setOnClickListener { onActionClick?.invoke() }
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.bind(viewHolder, position, payloads)
        else {
            val payload = payloads.firstOrNull() ?: return
            if (payload is UserSubscribeButton.Action) {
                viewHolder.btnAction.setAction(payload)
            }
        }
    }

    override fun getLayout() = R.layout.item_user

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserItem) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (avatar != other.avatar) return false
        if (action != other.action) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + (action?.hashCode() ?: 0)
        return result
    }
}