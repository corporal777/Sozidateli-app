package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemChatUnreadLabelBinding
import com.xwray.groupie.viewbinding.BindableItem


class ChatUnreadLabelItem(
    private val count: Int
) : BindableItem<ItemChatUnreadLabelBinding>() {

    override fun bind(viewBinding: ItemChatUnreadLabelBinding, position: Int) {
        viewBinding.apply {
            tvNewMessages.apply {
                text =
                    resources.getQuantityString(R.plurals.chat_new_messages_count, count, count)
            }
        }
    }

    override fun initializeViewBinding(view: View) = ItemChatUnreadLabelBinding.bind(view)
    override fun getLayout() = R.layout.item_chat_unread_label
}