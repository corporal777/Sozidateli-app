package com.example.holders

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.example.R
import com.example.databinding.ItemProfileDataUserBinding
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.databinding.BindableItem
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
        private val writeMessageClickListener: () -> Unit,
        private val onAvatarClick: (ImageView) -> Unit
) : BindableItem<ItemProfileDataUserBinding>(id) {

    override fun bind(viewBinding: ItemProfileDataUserBinding, position: Int) {
        viewBinding.apply {
            ivAvatar.apply {
                transitionName = avatarUrl
                clipToOutline = true
                if (avatar != null) setImageBitmap(avatar)
                else setImageResource(R.drawable.avatar_placeholder_rectangle)

                setOnClickListener { onAvatarClick(this) }
            }
            tvName.text = name
            tvId.apply { text = resources.getString(R.string.profile_uid_small, uid) }
            setAction(btnChat, subscribeAction)
            btnChat.setOnClickListener { writeMessageClickListener() }
        }
    }

    override fun bind(
        viewBinding: ItemProfileDataUserBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        if (payloads.isNullOrEmpty()) super.bind(viewBinding, position, payloads)
        else viewBinding.apply {
            (payloads[0] as? UserSubscribeButton.Action)?.let {
                subscribeAction = it
                setAction(btnChat, it)
            }
        }
    }


    private fun setAction(chatButton: View, action: UserSubscribeButton.Action) {
        val isEnabled = action != UserSubscribeButton.Action.UNBLOCK
        val alpha = if (isEnabled) 1f else 0.6f
        chatButton.isEnabled = isEnabled
        chatButton.alpha = alpha
    }

    override fun getLayout() = R.layout.item_profile_data_user
}