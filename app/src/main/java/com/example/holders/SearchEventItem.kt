package com.example.holders

import android.content.res.ColorStateList
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.example.R
import com.example.data.models.Event
import com.example.extensions.dateFormatterShortMothShortYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_search_event.*
import parseColor

class SearchEventItem(
        private val event: Event,
        private val onEventClick: () -> Unit
) : Item(event.id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventName.text = event.name
            tvEventDate.apply {
                val start = event.conferenceStart
                val finish = event.conferenceFinish

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

            tvImageName.apply {
                text = event.name
                clipToOutline = true
                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(event.backgroundColor.parseColor()
                        ?: ResourcesCompat.getColor(resources, R.color.colorAccent, null)))
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