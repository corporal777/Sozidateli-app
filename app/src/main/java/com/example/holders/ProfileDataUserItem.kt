package com.example.holders

import android.graphics.Bitmap
import android.view.View
import com.example.R
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_user.*

class ProfileDataUserItem(
        id: Long,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private var subscribeAction: UserSubscribeButton.Action,
        private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
        private val writeMessageClickListener: () -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            ivAvatar.apply {
                if (avatar != null) setImageBitmap(avatar)
                else setImageResource(R.drawable.avatar_placeholder)
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
                setAction(btnAction, btnChat, it)
            }
        }
    }

    private fun setAction(subscribeButton: UserSubscribeButton, newChatButton: View, action: UserSubscribeButton.Action) {
        when (action) {
            UserSubscribeButton.Action.FAVORITE -> {
                subscribeButton.setActionFavorite()
                newChatButton.isEnabled = true
            }
            UserSubscribeButton.Action.UNFAVORITE -> {
                subscribeButton.setActionUnfavorite()
                newChatButton.isEnabled = true
            }
            UserSubscribeButton.Action.UNBLOCK -> {
                subscribeButton.setActionUnblock()
                newChatButton.isEnabled = false
            }
            else -> {
                subscribeButton.setActionFavorite()
                newChatButton.isEnabled = false
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_user
}