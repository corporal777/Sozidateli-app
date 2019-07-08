package com.example.holders

import com.example.R
import com.example.data.models.user.User
import com.example.util.weak
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_user.*
import setCircleImageWithPlaceholder

class UserItem(
        private val user: User,
        onUserClick: () -> Unit
) : Item(user.user_id.toLong()) {

    private val clickListener by weak(onUserClick)

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = user.fullName
            ivUserAvatar.setCircleImageWithPlaceholder(user.user_avatar, R.drawable.avatar_placeholder)
            root.setOnClickListener { clickListener?.invoke() }
        }
    }

    override fun getLayout() = R.layout.item_user
}