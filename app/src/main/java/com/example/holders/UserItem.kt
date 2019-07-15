package com.example.holders

import com.example.R
import com.example.util.weak
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_user.*
import setCircleImageWithPlaceholder

class UserItem(
        private val id: Int,
        private val name: String,
        private val avatar: String?,
        onUserClick: () -> Unit
) : Item(id.toLong()) {

    private val clickListener by weak(onUserClick)

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = name
            ivUserAvatar.setCircleImageWithPlaceholder(avatar, R.drawable.avatar_placeholder)
            itemView.setOnClickListener { clickListener?.invoke() }
        }
    }

    override fun getLayout() = R.layout.item_user
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserItem) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (avatar != other.avatar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (avatar?.hashCode() ?: 0)
        return result
    }
}