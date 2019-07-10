package com.example.holders

import com.example.R
import com.example.util.weak
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_user.*
import setCircleImageWithPlaceholder

class UserItem(
        private val name: String,
        private val avatar: String?,
        onUserClick: () -> Unit
) : Item() {

    private val clickListener by weak(onUserClick)

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = name
            ivUserAvatar.setCircleImageWithPlaceholder(avatar, R.drawable.avatar_placeholder)
            root.setOnClickListener { clickListener?.invoke() }
        }
    }

    override fun getLayout() = R.layout.item_user
}