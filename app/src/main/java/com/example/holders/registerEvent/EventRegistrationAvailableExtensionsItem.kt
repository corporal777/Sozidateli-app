package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_registration_field_title.*

class EventRegistrationAvailableExtensionsItem(
        private val title: String?
) : Item(title.hashCode().toLong()) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textView.apply {
                text = title
                isVisible = !title.isNullOrEmpty()
            }
        }
    }

    override fun getLayout() = R.layout.item_event_registration_field_title

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationAvailableExtensionsItem) return false

        if (title != other.title) return false

        return true
    }
}