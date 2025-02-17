package com.example.holders

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemUserBinding
import com.example.ui.views.UserSubscribeButton
import com.example.util.setCircleAvatar
import com.xwray.groupie.viewbinding.BindableItem

class UserItem(
    private val id: Int,
    private val name: String,
    private val description: String?,
    private val avatar: String?,
    private val onUserClick: () -> Unit,
    var action: UserSubscribeButton.Action? = null,
    private val onActionClick: (() -> Unit)? = null
) : BindableItem<ItemUserBinding>(id.toLong()) {

    override fun bind(viewBinding: ItemUserBinding, position: Int) {
        viewBinding.apply {
            tvUserName.text = name
            tvDescription.apply {
                isVisible = !description.isNullOrEmpty()
                text = description
            }
            ivUserAvatar.setCircleAvatar(avatar)
            root.setOnClickListener { onUserClick.invoke() }
            btnAction.apply {
                val action = this@UserItem.action
                visibility = if (action != null) {
                    setAction(action)
                    setOnClickListener { onActionClick?.invoke() }
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }


    override fun bind(viewBinding: ItemUserBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) super.bind(viewBinding, position, payloads)
        else {
            val payload = payloads.firstOrNull() ?: return
            if (payload is UserSubscribeButton.Action) {
                viewBinding.btnAction.setAction(payload)
            }
        }
    }

    override fun initializeViewBinding(view: View) = ItemUserBinding.bind(view)
    override fun getLayout() = R.layout.item_user

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserItem) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (avatar != other.avatar) return false
        if (action != other.action) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + (action?.hashCode() ?: 0)
        return result
    }
}