package com.example.ui.event.registration.items

import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemRegisterEventTitleBinding
import com.xwray.groupie.databinding.BindableItem

class RegisterEventTitleItem(
    private val title: String?
) : BindableItem<ItemRegisterEventTitleBinding>(title.hashCode().toLong()) {

    override fun bind(viewBinding: ItemRegisterEventTitleBinding, position: Int) {
        viewBinding.apply {
            textView.apply {
                text = title
                isVisible = !title.isNullOrEmpty()
            }
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is RegisterEventTitleItem) return false
        if (title != other.title) return false
        return true
    }

    override fun getLayout() = R.layout.item_register_event_title

}