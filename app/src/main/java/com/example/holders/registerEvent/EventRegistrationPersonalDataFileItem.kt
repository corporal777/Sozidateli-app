package com.example.holders.registerEvent

import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_registration_field_file.*

class EventRegistrationPersonalDataFileItem(
        private val url: String?,
        private val description: String?,
        private val onFileClickListener: OnPersonalDataFileClickListener
) : Item((url.hashCode() + description.hashCode()).toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
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

            itemView.apply {
                setOnClickListener { url?.let { onFileClickListener(it) } }
            }
        }
    }

    override fun getLayout() = R.layout.item_event_registration_field_file

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationPersonalDataFileItem) return false

        if (description != other.description) return false
        if (url != other.url) return false

        return true
    }
}

typealias OnPersonalDataFileClickListener = (url: String) -> Unit