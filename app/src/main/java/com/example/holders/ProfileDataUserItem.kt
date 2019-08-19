package com.example.holders

import android.graphics.Bitmap
import android.view.View
import com.example.R
import com.example.ui.views.UserSubscribeButton
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_SUBSCRIBE
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNBLOCK
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNSUBSCRIBE
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_user.*
import kotlinx.android.synthetic.main.item_profile_data_user.ivAvatar
import kotlinx.android.synthetic.main.item_profile_data_user.tvName
import kotlinx.android.synthetic.main.item_profile_data_user_edit.*
import setCircleImage

class ProfileDataUserItem(
        id: Long,
        private val avatar: Bitmap?,
        private val name: String,
        private val uid: Int,
        private var subscribeAction: Int,
        private val actionClickListener: (Int) -> Unit,
        private val writeMessageClickListener: () -> Unit
) : Item(id) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
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

    override fun bind(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) super.bind(holder, position, payloads)
        else holder.apply {
            (payloads[0] as? Int)?.let {
                subscribeAction = it
                setAction(btnAction, btnChat, it)
            }
        }
    }

    private fun setAction(subscribeButton: UserSubscribeButton, newChatButton: View, action: Int) {
        when (action) {
            ACTION_UNBLOCK -> {
                subscribeButton.setActionUnblock()
                newChatButton.isEnabled = false
            }
            ACTION_SUBSCRIBE -> {
                subscribeButton.setActionSubscribe()
                newChatButton.isEnabled = true
            }
            ACTION_UNSUBSCRIBE -> {
                subscribeButton.setActionUnsubscribe()
                newChatButton.isEnabled = true
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_user
}