package com.example.ui.user.items

import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ItemProfileUserActionsBinding
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.databinding.BindableItem

class UserProfileActionsItem(
    val title: Any?,
    var action: () -> Unit
) : BindableItem<ItemProfileUserActionsBinding>() {

    var isAction = false

    override fun bind(viewBinding: ItemProfileUserActionsBinding, position: Int) {
        viewBinding.apply {
            tvAction.apply {
                if (title != null) {
                    if (title is String) {
                        isAction = false
                        text = title
                    } else {
                        if (title is UserSubscribeButton.Action) {
                            isAction = true
                            decorTitle(this, title)
                        }
                    }
                }
                setOnClickListener {
                    action.invoke()
                }
            }
        }
    }

    private fun decorTitle(textView: TextView, title: UserSubscribeButton.Action?) {
        textView.apply {
            text = if (title == UserSubscribeButton.Action.UNBLOCK) {
                context.getString(R.string.unblock_user)
            } else {
                context.getString(R.string.block_user)
            }
        }
    }

    override fun bind(
        viewBinding: ItemProfileUserActionsBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is UserSubscribeButton.Action) {
                decorTitle(viewBinding.tvAction, payload)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_profile_user_actions
}