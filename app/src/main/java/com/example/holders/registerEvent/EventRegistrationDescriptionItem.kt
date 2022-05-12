package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_registration_field_title.*

class EventRegistrationDescriptionItem(
        private val description: String?
) : Item(description.hashCode().toLong()) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textView.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }
        }
    }

    override fun getLayout() = R.layout.item_event_registration_field_description

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationDescriptionItem) return false

        if (description != other.description) return false

        return true
    }
}