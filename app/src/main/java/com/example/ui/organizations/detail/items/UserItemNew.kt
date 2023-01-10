package com.example.ui.organizations.redesign.items

import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.ItemOrganizationHeaderBinding
import com.example.databinding.ItemUserBinding
import com.example.holders.UserItem
import com.example.ui.views.UserSubscribeButton
import com.example.util.setCircleAvatar
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.item_user.*
import setCircleImage

class UserItemNew(
    private val user: Int,
    private val name: String,
    private val description: String?,
    private val avatar: String?,
    private val isFavorite : Boolean,
    private val isCurrentUser : Boolean,
    private val onUserClick: (id : Int) -> Unit,
    private val onActionClick: (id : Int) -> Unit
) : BindableItem<ItemUserBinding>(user.toLong()) {

    private var isSubscribed = isFavorite

    override fun bind(viewBinding: ItemUserBinding, position: Int) {
        viewBinding.apply {
            tvUserName.text = name
            tvDescription.apply {
                isVisible = !description.isNullOrEmpty()
                text = description
            }
            ivUserAvatar.setCircleAvatar(avatar)

            root.setOnClickListener { onUserClick.invoke(user) }
            btnAction.apply {
                isVisible = !isCurrentUser
                setSubscribed(isSubscribed, this)
                setOnClickListener {
                    onActionClick.invoke(user)
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

    private fun setSubscribed(isSubscribed: Boolean, button: UserSubscribeButton) {
        button.setActionNew(isSubscribed)
    }

    override fun getLayout(): Int = R.layout.item_user
}