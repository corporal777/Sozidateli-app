package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.util.setCircleAvatar
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_organization_user.*
import setCircleImage

class OrganizationUserItem(
        id: Int,
        private val name: String,
        private val avatar: String?,
        private val description: String?,
        private val onUserClick: () -> Unit
) : Item(id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvUserName.text = name
            ivUserAvatar.setCircleAvatar(avatar)
            tvDescription.apply {
                isVisible = !description.isNullOrEmpty()
                text = description
            }
            itemView.setOnClickListener { onUserClick() }
        }
    }

    override fun getLayout() = R.layout.item_organization_user
}