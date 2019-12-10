package com.example.holders

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.example.R
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_user.*

class ProfileDataUserItem(
        id: Long,
        private val avatarUrl: String?,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private var subscribeAction: UserSubscribeButton.Action,
        private var isInFavorite: Boolean,
        private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
        private val writeMessageClickListener: () -> Unit,
        private val onAvatarClick: (ImageView) -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            ivAvatar.apply {
                transitionName = avatarUrl
                clipToOutline = true
                if (avatar != null) setImageBitmap(avatar)
                else setImageResource(R.drawable.avatar_placeholder_rectangle)

                setOnClickListener { onAvatarClick(this) }
            }
            tvName.text = name
            tvId.apply { text = resources.getString(R.string.profile_uid, uid) }
            btnAction.apply {
                setAction(this, btnChat, subscribeAction)
                setOnClickListener { actionClickListener(this.action) }
            }

            btnChat.setOnClickListener { writeMessageClickListener() }
        }
    }

    override fun bind(holder: GroupieViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) super.bind(holder, position, payloads)
        else holder.apply {
            (payloads[0] as? UserSubscribeButton.Action)?.let {
                subscribeAction = it
                if (it == UserSubscribeButton.Action.FAVORITE) isInFavorite = false
                else if (it == UserSubscribeButton.Action.UNFAVORITE) isInFavorite = true
                setAction(btnAction, btnChat, it)
            }
        }
    }

    private fun setAction(subscribeButton: UserSubscribeButton, newChatButton: View, action: UserSubscribeButton.Action) {
        val isEnabled = action != UserSubscribeButton.Action.UNBLOCK
        val alpha = if (isEnabled) 1f else 0.6f
        subscribeButton.isEnabled = isEnabled
        newChatButton.isEnabled = isEnabled
        subscribeButton.alpha = alpha
        newChatButton.alpha = alpha

        val finalAction = when {
            isEnabled -> action
            isInFavorite -> UserSubscribeButton.Action.UNFAVORITE
            else -> UserSubscribeButton.Action.FAVORITE
        }

        subscribeButton.setAction(finalAction)
    }

    override fun getLayout() = R.layout.item_profile_data_user
}