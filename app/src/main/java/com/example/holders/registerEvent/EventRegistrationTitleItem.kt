package com.example.holders.registerEvent

import android.widget.TextView
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class EventRegistrationTitleItem(
        private val title: String
) : Item(title.hashCode().toLong()) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        (viewHolder.itemView as TextView).text = title
    }

    override fun getLayout() = R.layout.item_event_registration_field_title

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationTitleItem) return false

        if (title != other.title) return false

        return true
    }
}