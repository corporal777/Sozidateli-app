package com.example.holders.registerEvent

import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.R
import com.example.databinding.ItemEventRegistrationFieldTitleBinding
import com.xwray.groupie.databinding.BindableItem

class EventRegistrationTitleItem(
    private val title: String?
) : BindableItem<ItemEventRegistrationFieldTitleBinding>(title.hashCode().toLong()) {

    override fun bind(viewBinding: ItemEventRegistrationFieldTitleBinding, position: Int) {
        viewBinding.apply {
            textContainer.apply {
                updatePadding(
                    // top = resources.getDimensionPixelSize(R.dimen.event_registration_form_title_margin_top),
                    top = resources.getDimensionPixelSize(R.dimen.event_registration_form_title_margin_bottom),
                    bottom = resources.getDimensionPixelSize(R.dimen.event_registration_form_title_margin_bottom)
                )
            }

            textView.apply {
                text = title
                isVisible = !title.isNullOrEmpty()
            }
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationTitleItem) return false
        if (title != other.title) return false
        return true
    }

    override fun getLayout() = R.layout.item_event_registration_field_title

}