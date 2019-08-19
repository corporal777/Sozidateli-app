package com.example.holders

import android.graphics.Bitmap
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_current_user.*
import setCircleImage

class ProfileDataUserEditableItem(
        id: Long,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private val editClickListener: () -> Unit
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
        }
    }

    override fun getLayout() = R.layout.item_profile_data_current_user
}