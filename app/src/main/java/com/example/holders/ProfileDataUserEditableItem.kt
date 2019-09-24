package com.example.holders

import android.graphics.Bitmap
import com.example.R
import com.example.data.models.user.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_current_user.*
import setUserStatus

class ProfileDataUserEditableItem(
        id: Long,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private val status: User.Status,
        private val editClickListener: () -> Unit,
        private val statusClickListener: () -> Unit
) : Item(id) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            ivAvatar.apply {
                if (avatar != null) setImageBitmap(avatar)
                else setImageResource(R.drawable.avatar_placeholder)
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

    override fun getLayout() = R.layout.item_profile_data_current_user
}