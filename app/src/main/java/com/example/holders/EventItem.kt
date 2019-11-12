package com.example.holders

import android.graphics.PorterDuff
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event.*
import parseColor
import java.text.SimpleDateFormat
import java.util.*

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
                val overlay = ResourcesCompat.getColor(resources, R.color.auth_background_overlay, null)
                val color = event.backgroundColor.parseColor() ?: overlay
                setBackgroundColor(color)
                Picasso.get().load(event.logo).into(this)
                setColorFilter(overlay, PorterDuff.Mode.DARKEN)
            }

            tvEventAddress.apply {
                text = event.address
            }
            tvEventLabel.text = event.name
            tvEventDate.apply {
                val start = event.conferenceStart
                val formatted = start?.parseAndFormat(defaultServerDateFormatter, SimpleDateFormat(DATE_FORMAT_FULL_MONTH_FULL_YEAR, Locale.getDefault()))
                val date = "$formatted г."
                text = date
            }

            tvEventType.apply {
                text = event.format?.name
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