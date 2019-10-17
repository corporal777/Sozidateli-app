package com.example.holders

import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.models.Event
import com.example.extensions.dateFormatterShortMothShortYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event.*

class EventItem(
        private val event: Event,
        private val onEventClick: () -> Unit,
        private val onGoToEventClick: () -> Unit
) : Item(event.id.toLong()) {

    var isInHorizontalParent = false

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemContainer.apply {
                clipToOutline = true
//                alpha = if (event.status === Event.RegistrationStatus.CONFERENCE_ENDS) 0.5f else 1f

                setOnClickListener { onEventClick() }

                if (isInHorizontalParent) {
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }

            ivBackground.apply {
                Picasso.get().load(event.logo).placeholder(R.mipmap.ic_launcher_background).into(this)
                setColorFilter(ResourcesCompat.getColor(resources, R.color.auth_background_overlay, null), PorterDuff.Mode.DARKEN)
            }

            tvOrganizationLabel.apply {
                text = event.organization?.name
            }
            tvEventLabel.text = event.name
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

            when (event.status) {
                null -> showRegisterToEvent(viewHolder)
                else -> showApproveStatus(viewHolder, event)
            }
        }
    }

    private fun showRegisterToEvent(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            tvStatus.visibility = View.GONE
            btnGoToEvent.apply {
                setOnClickListener { onGoToEventClick() }
                visibility = View.VISIBLE
            }
        }
    }

    private fun showApproveStatus(viewHolder: GroupieViewHolder, event: Event) {
        viewHolder.apply {
            btnGoToEvent.apply { visibility = View.GONE }
            tvStatus.apply {
                visibility = View.VISIBLE

                val textColor: Int
                val textBackground: Int
                val textRes: Int
                when (event.status) {
                    Event.RegistrationStatus.APPROVED -> {
                        textColor = R.color.event_status_approved_text
                        textBackground = R.color.event_status_approved_background
                        textRes = R.string.event_status_approved
                    }
                    Event.RegistrationStatus.DECLINED -> {
                        textColor = R.color.event_status_wait_confirmation_text
                        textBackground = R.color.red
                        textRes = R.string.event_status_decline
                    }
//                    Event.RegistrationStatus.CONFERENCE_ENDS -> {
//                        textColor = R.color.event_status_finished_text
//                        textBackground = R.color.event_status_finished_background
//                        textRes = R.string.event_status_finished
//                    }
                    else -> {
                        textColor = R.color.event_status_wait_confirmation_text
                        textBackground = R.color.event_status_wait_confirmation_background
                        textRes = R.string.event_status_wait_confirmation
                    }


                }

                text = resources.getString(textRes)
                setTextColor(ContextCompat.getColor(context, textColor))
                setBackgroundColor(ContextCompat.getColor(context, textBackground))
            }
        }
    }

    override fun getLayout() = R.layout.item_event
}