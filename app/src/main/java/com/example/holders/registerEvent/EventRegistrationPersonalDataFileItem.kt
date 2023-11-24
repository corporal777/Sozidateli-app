package com.example.holders.registerEvent

import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.R
import com.example.databinding.ItemEventRegistrationFieldFileBinding
import com.xwray.groupie.databinding.BindableItem

class EventRegistrationPersonalDataFileItem(
    private val url: String?,
    private val description: String?,
    private val onFileClickListener: OnPersonalDataFileClickListener
) : BindableItem<ItemEventRegistrationFieldFileBinding>((url.hashCode() + description.hashCode()).toLong()) {

    override fun bind(viewBinding: ItemEventRegistrationFieldFileBinding, position: Int) {
        viewBinding.apply {
            val hasText = !description.isNullOrEmpty()
            textContainer.apply {
                updatePadding(
                    top = if (hasText) resources.getDimensionPixelSize(R.dimen.event_registration_file_margin_top) else 0,
                    bottom = resources.getDimensionPixelSize(R.dimen.event_registration_file_margin_bottom)
                )
            }

            textView.apply {
                text = description
                isVisible = hasText
            }
            root.apply {
                setOnClickListener { url?.let { onFileClickListener(it) } }
            }
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationPersonalDataFileItem) return false

        if (description != other.description) return false
        if (url != other.url) return false

        return true
    }


    override fun getLayout() = R.layout.item_event_registration_field_file
}

typealias OnPersonalDataFileClickListener = (url: String) -> Unit