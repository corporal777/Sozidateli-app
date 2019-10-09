package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_organization.tvDescription
import kotlinx.android.synthetic.main.item_organization_user.*
import kotlinx.android.synthetic.main.item_user.ivUserAvatar
import kotlinx.android.synthetic.main.item_user.tvUserName
import setCircleImage

class SearchUserItem(
        id: Int,
        private val name: String,
        private val avatar: String?,
        private val description: String?,
        private val onUserClick: () -> Unit
) : Item(id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = name
            ivUserAvatar.setCircleImage(avatar, R.drawable.avatar_placeholder)
            tvDescription.apply {
                isVisible = !description.isNullOrEmpty()
                text = description
            }
            itemView.setOnClickListener { onUserClick() }

            ivNext.isVisible = false
        }
    }

    override fun getLayout() = R.layout.item_organization_user
}