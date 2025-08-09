package com.example.holders

import android.view.View
import android.widget.ImageView
import coil.transform.RoundedCornersTransformation
import com.example.app.R
import com.example.app.databinding.ItemProfileDataUserBinding
import com.example.common.dp
import com.example.ui.views.UserSubscribeButton
import com.example.util.setImage
import com.xwray.groupie.viewbinding.BindableItem


class ProfileDataUserItem(
        id: Long,
        private val avatarUrl: String?,
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
                setImage(
                    image = avatarUrl ?: R.drawable.avatar_placeholder_rectangle,
                    transformations = listOf(RoundedCornersTransformation(10f.dp))
                )
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
        payloads: MutableList<Any>
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

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is ProfileDataUserItem) return false
        if (avatarUrl != other.avatarUrl) return false
        if (name != other.name) return false
        if (uid != other.uid) return false
        if (subscribeAction != other.subscribeAction) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemProfileDataUserBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_user
}