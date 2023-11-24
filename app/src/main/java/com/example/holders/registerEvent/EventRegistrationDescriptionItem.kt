package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemEventRegistrationFieldDescriptionBinding
import com.xwray.groupie.databinding.BindableItem

class EventRegistrationDescriptionItem(
    private val description: String?
) : BindableItem<ItemEventRegistrationFieldDescriptionBinding>(description.hashCode().toLong()) {

    override fun bind(viewBinding: ItemEventRegistrationFieldDescriptionBinding, position: Int) {
        viewBinding.apply {
            textView.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationDescriptionItem) return false
        if (description != other.description) return false
        return true
    }

    override fun getLayout() = R.layout.item_event_registration_field_description

}