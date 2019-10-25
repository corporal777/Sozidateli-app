package com.example.holders.registerEvent

import android.widget.TextView
import com.example.R
import com.example.extensions.setUnderlineSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class EventRegistrationPersonalDataFileItem(
        private val url: String,
        private val description: String,
        private val onFileClickListener: OnPersonalDataFileClickListener
) : Item((url.hashCode() + description.hashCode()).toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        (viewHolder.itemView as TextView).apply {
            text = description.setUnderlineSpan()

            setOnClickListener { onFileClickListener(url) }
        }
    }

    override fun getLayout() = R.layout.item_event_registration_field_title

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationPersonalDataFileItem) return false

        if (url != other.url) return false

        return true
    }
}

typealias OnPersonalDataFileClickListener = (url: String) -> Unit