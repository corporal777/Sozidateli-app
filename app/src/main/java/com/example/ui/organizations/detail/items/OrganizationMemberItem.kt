package com.example.ui.organizations.detail.items

import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemUserBinding
import com.example.ui.views.UserSubscribeButton
import com.example.util.setCircleAvatar
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class OrganizationMemberItem(
    private val user: Int?,
    private val name: String?,
    private val description: String?,
    private val avatar: String?,
    private val isFavorite: Boolean,
    private val isCurrentUser: Boolean,
    private val onUserClick: (id: Int) -> Unit,
    private val onActionClick: (id: Int) -> Unit
) : BindableItem<ItemUserBinding>(user?.toLong() ?: 0) {

    private var isSubscribed = isFavorite

    override fun bind(viewBinding: ItemUserBinding, position: Int) {
        viewBinding.apply {
            tvUserName.text = name
            tvDescription.apply {
                isVisible = !description.isNullOrEmpty()
                text = description
            }
            ivUserAvatar.setCircleAvatar(avatar)

            root.setOnClickListener { onUserClick.invoke(user ?: 0) }
            btnAction.apply {
                isVisible = !isCurrentUser
                setSubscribed(isSubscribed, this)
                setOnClickListener {
                    onActionClick.invoke(user ?: 0)
                }
            }

        }
    }

    override fun bind(
        viewBinding: ItemUserBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) {
                isSubscribed = payload
                setSubscribed(isSubscribed, viewBinding.btnAction)
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is OrganizationMemberItem) return false
        if (user != other.user) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (avatar != other.avatar) return false
        if (isFavorite != other.isFavorite) return false
        if (isCurrentUser != other.isCurrentUser) return false
        return true
    }

    private fun setSubscribed(isSubscribed: Boolean, button: UserSubscribeButton) {
        button.setActionNew(isSubscribed)
    }

    override fun getLayout(): Int = R.layout.item_user
}