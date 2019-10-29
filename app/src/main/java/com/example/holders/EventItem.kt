package com.example.holders

import android.graphics.PorterDuff
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.extensions.dateFormatterShortMothShortYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToInterval
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
                    event.conferenceStart?.formatToInterval(event.conferenceFinish)
                } else {
                    start?.parseAndFormat(parser, formatter)
                }
            }

            if (event.isCanRegister()) showRegisterToEvent(viewHolder)
            else showEventStatus(viewHolder, event)
        }
    }

    private fun showRegisterToEvent(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            tvStatus.isVisible = false
            btnGoToEvent.apply {
                setOnClickListener { onGoToEventClick() }
                isVisible = true
            }
        }
    }

    private fun showEventStatus(viewHolder: GroupieViewHolder, event: Event) {
        viewHolder.apply {
            btnGoToEvent.isVisible = false
            tvStatus.apply {
                val textColor: Int
                val textBackground: Int
                val textRes: Int
                when (event.status) {
                    Event.Status.CONFERENCE_ENDS -> {
                        textColor = R.color.event_status_finished_text
                        textBackground = R.color.event_status_finished_background
                        textRes = R.string.event_status_finished
                    }
                    else -> when (event.userRegistration) {
                        Event.RegistrationStatus.APPROVED -> {
                            textColor = R.color.event_status_approved_text
                            textBackground = R.color.event_status_approved_background
                            textRes = R.string.event_status_approved
                        }
                        Event.RegistrationStatus.PENDING -> {
                            textColor = R.color.event_status_wait_confirmation_text
                            textBackground = R.color.event_status_wait_confirmation_background
                            textRes = R.string.event_status_wait_confirmation
                        }
                        Event.RegistrationStatus.CANCELLED,
                        Event.RegistrationStatus.DECLINED -> {
                            textColor = R.color.event_status_wait_confirmation_text
                            textBackground = R.color.attention_action
                            textRes = R.string.event_status_decline
                        }
                        else -> {
                            tvStatus.isVisible = false
                            return
                        }
                    }
                }

                text = resources.getString(textRes)
                setTextColor(ContextCompat.getColor(context, textColor))
                setBackgroundColor(ContextCompat.getColor(context, textBackground))
                isVisible = true
            }
        }
    }

    override fun getLayout() = R.layout.item_event
}