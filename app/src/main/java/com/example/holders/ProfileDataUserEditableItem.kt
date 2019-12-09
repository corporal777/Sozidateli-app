package com.example.holders

import android.graphics.Bitmap
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.user.User
import com.example.extensions.dp
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_current_user.*

class ProfileDataUserEditableItem(
        id: Long,
        private val avatarUrl: String?,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private val status: User.Status,
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

            val statusIcon = when (status) {
                User.Status.LOW_PROTECTION -> R.drawable.ic_user_status_low
                User.Status.MID_PROTECTION -> R.drawable.ic_user_status_middle
                User.Status.MAX_PROTECTION -> R.drawable.ic_user_status_max
            }

            tvName.apply {
                val imageSpan = statusIcon.let { ContextCompat.getDrawable(context, statusIcon) }?.let {
                    it.setBounds(0, 0, 16.dp, 16.dp)
                    ImageSpan(it, ImageSpan.ALIGN_BASELINE)
                }

                if (imageSpan == null) {
                    tvName.text = name
                } else {
                    val titleSpannable = SpannableStringBuilder(name).apply {
                        append("  ").setSpan(imageSpan, length - 1, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    }

                    tvName.text = titleSpannable
                }
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