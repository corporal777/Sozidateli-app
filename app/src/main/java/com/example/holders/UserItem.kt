package com.example.holders

import android.view.View
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_user.*
import setCircleImage

class UserItem(
        private val id: Int,
        private val name: String,
        private val avatar: String?,
        private val onUserClick: () -> Unit,
        private val action: Int? = null,
        private val onActionClick: (() -> Unit)? = null
) : Item(id.toLong()) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = name
            ivUserAvatar.setCircleImage(avatar, R.drawable.avatar_placeholder)
            itemView.setOnClickListener { onUserClick.invoke() }
            btnAction.apply {
                visibility = if (this@UserItem.action != null) {
                    setAction(this@UserItem.action)
                    setOnClickListener { onActionClick?.invoke() }
                    View.VISIBLE
                } else {
                    View.GONE
                }
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
        result = 31 * result + (action ?: 0)
        return result
    }
}