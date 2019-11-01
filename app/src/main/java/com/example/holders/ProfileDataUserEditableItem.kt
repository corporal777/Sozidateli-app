package com.example.holders

import android.graphics.Bitmap
import android.widget.ImageView
import com.example.R
import com.example.data.models.user.User
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_current_user.*
import setUserStatus

class ProfileDataUserEditableItem(
        id: Long,
        private val avatarUrl: String?,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private val status: User.Status,
        private val editClickListener: () -> Unit,
        private val statusClickListener: () -> Unit,
        private val onAvatarClick: (ImageView) -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            ivAvatar.apply {
                transitionName = avatarUrl
                if (avatar != null) setImageBitmap(avatar)
                else setImageResource(R.drawable.avatar_placeholder)
                setOnClickListener { onAvatarClick(this) }
            }
            tvName.text = name
            tvId.apply { text = resources.getString(R.string.profile_uid, uid) }

            btnEdit.setOnClickListener { editClickListener() }

            btnStatus.apply {
                setUserStatus(status, resources.getString(R.string.profile_user_status))
                setOnClickListener { statusClickListener() }
            }
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (!super.isSameAs(other)) return false
        if (other !is ProfileDataUserEditableItem) return false
        if (other.avatar != avatar) return false
        if (other.name != name) return false
        if (other.uid != uid) return false
        if (other.status != status) return false
        return true
    }

    override fun getLayout() = R.layout.item_profile_data_current_user
}