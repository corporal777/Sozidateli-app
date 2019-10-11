package com.example.holders

import com.example.R
import com.example.data.models.Event
import com.example.extensions.dateFormatterShortMothShortYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_search_event.*

class SearchEventItem(
        private val event: Event,
        private val onEventClick: () -> Unit
) : Item(event.id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventName.text = event.name
            tvEventDate.apply {
                val start = event.conference_start
                val finish = event.conference_finish

                val parser = defaultServerDateFormatter
                val formatter = dateFormatterShortMothShortYear
                text = if (start != null && finish != null) {
                    "${start.parseAndFormat(parser, formatter)} - ${finish.parseAndFormat(parser, formatter)}"
                } else {
                    start?.parseAndFormat(parser, formatter)
                }
            }

            ivLogo.apply {
                clipToOutline = true
                Picasso.get().load(event.logo.let { if (it.isNullOrBlank()) null else it })
                        .into(this)
            }

            itemView.setOnClickListener { onEventClick.invoke() }
        }
    }

    override fun getLayout() = R.layout.item_search_event

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SearchEventItem) return false

        if (event != other.event) return false

        return true
    }

    override fun hashCode(): Int {
        return event.hashCode()
    }
}