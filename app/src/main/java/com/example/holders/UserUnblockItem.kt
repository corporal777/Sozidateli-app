package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_user_unblock.*
import setCircleImage

class UserUnblockItem(
        private val id: Int,
        private val name: String,
        private val avatar: String?,
        private val onUserClick: () -> Unit,
        private val onUnblockClick: () -> Unit
) : Item(id.toLong()) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = name
            ivUserAvatar.setCircleImage(avatar, R.drawable.avatar_placeholder)
            itemView.setOnClickListener { onUserClick.invoke() }
            btnUnblock.setOnClickListener { onUnblockClick() }
        }
    }

    override fun getLayout() = R.layout.item_user_unblock

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserUnblockItem) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}