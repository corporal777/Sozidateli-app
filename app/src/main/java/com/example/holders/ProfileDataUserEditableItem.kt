package com.example.holders

import android.graphics.Bitmap
import android.widget.ImageView
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_current_user.*

class ProfileDataUserEditableItem(
        id: Long,
        private val avatarUrl: String?,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private val editClickListener: () -> Unit,
        private val onAvatarClick: (ImageView) -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            ivAvatar.apply {
                clipToOutline = true
                transitionName = avatarUrl
                if (avatar != null) setImageBitmap(avatar)
                else setImageResource(R.drawable.avatar_placeholder_rectangle)
                setOnClickListener { onAvatarClick(this) }
            }
            tvId.apply { text = resources.getString(R.string.profile_uid, uid) }

            btnEdit.setOnClickListener { editClickListener() }

            tvName.apply {
                tvName.text = name
            }
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (!super.isSameAs(other)) return false
        if (other !is ProfileDataUserEditableItem) return false
        if (other.avatar != avatar) return false
        if (other.name != name) return false
        if (other.uid != uid) return false
        return true
    }

    override fun getLayout() = R.layout.item_profile_data_current_user
}