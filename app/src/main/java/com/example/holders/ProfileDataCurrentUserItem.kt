package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_current_user.*
import setCircleImage

class ProfileDataCurrentUserItem(
        id: Long,
        private val avatar: String?,
        private val name: String,
        private val uid: Int,
        private val editClickListener: () -> Unit
) : Item(id) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            ivAvatar.setCircleImage(avatar, R.drawable.avatar_placeholder)
            tvName.text = name
            tvId.apply { text = resources.getString(R.string.profile_uid, uid) }

            btnEdit.setOnClickListener { editClickListener() }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_current_user
}