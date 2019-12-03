package com.example.holders

import android.graphics.Color
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormat
import com.example.extensions.parseToDate
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event.*
import parseColor
import java.text.SimpleDateFormat
import java.util.*

class EventItem(
        private val event: Event,
        private val onEventClickListener: OnEventClickListener
) : Item(event.id.toLong()) {

    var isInHorizontalParent = false

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.apply {
                if (isInHorizontalParent) {
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }
            itemContainer.apply {
                alpha = if (event.status == Event.Status.CONFERENCE_ENDS) 0.4f else 1f
            }

            flAction.apply {
                clipToOutline = true
                setOnClickListener { onEventClickListener.onShowEventClick(event) }
            }

            ivLogo.apply {
                val color = event.backgroundColor.parseColor()
                        ?: ResourcesCompat.getColor(resources, R.color.event_item_no_image_background, null)
                setBackgroundColor(color)
                Picasso.get().load(event.logo).into(this)
            }

            tvEventFormat.apply {
                val format = event.format?.name
                text = format
                isVisible = !format.isNullOrEmpty()
                setOnClickListener { onEventClickListener.onShowFilterClick(event) }
            }

            setApproveStatus(tvStatus)

            tvFinished.isVisible = event.status == Event.Status.CONFERENCE_ENDS

            setAction(btnEventAction)

            tvEventAddress.apply {
                text = event.address
            }
            tvEventLabel.text = event.name

            val dateStart = event.conferenceStart?.parseToDate(defaultServerDateFormatter)

            tvEventDay.apply {
                val formatter = SimpleDateFormat("d", Locale("ru", "RU"))
                val day = formatter.format(dateStart)
                text = day
            }

            tvEventDate.apply {
                val formatter = SimpleDateFormat("MMM\n''yy", Locale("ru", "RU"))
                val formatted = event.conferenceStart?.parseAndFormat(defaultServerDateFormatter, formatter)
                val result = formatted?.split("\n")?.mapIndexed { index, part ->
                    if (index == 0 && part.length > 3) part.substring(0, 3)
                    else part
                }?.joinToString("\n")
                text = result
            }
        }
    }

    private fun setApproveStatus(tvStatus: TextView) {
        tvStatus.apply {
            val textBackground: Int
            val textRes: Int
            when (event.userRegistration) {
                Event.RegistrationStatus.APPROVED -> {
                    textBackground = R.color.event_status_approved_background
                    textRes = R.string.event_status_approved
                }
                Event.RegistrationStatus.CANCELLED,
                Event.RegistrationStatus.PENDING -> {
                    textBackground = R.color.event_status_wait_confirmation_background
                    textRes = R.string.event_status_wait_confirmation
                }
                Event.RegistrationStatus.DECLINED -> {
                    textBackground = R.color.event_status_declined_background
                    textRes = R.string.event_status_decline
                }
                else -> {
                    isVisible = false
                    return
                }
            }

            text = resources.getString(textRes)
            backgroundTintList = ContextCompat.getColorStateList(context, textBackground)
            isVisible = true
        }
    }

    private fun setAction(btnAction: Button) {
        btnAction.apply {
            val textBackground: Int
            var textColor = Color.BLACK
            val textRes: Int
            val clickAction: () -> Unit
            if (event.status == Event.Status.CONFERENCE_ENDS) {
                isVisible = false
                return@apply
            } else when (event.userRegistration) {
                Event.RegistrationStatus.PENDING -> {
                    textBackground = R.drawable.background_event_action
                    textRes = R.string.event_action_cancel_request
                    clickAction = { onEventClickListener.onActionCancel(event) }
                }
                Event.RegistrationStatus.DECLINED -> {
                    if (event.organization?.emails.isNullOrEmpty()) {
                        isVisible = false
                        return@apply
                    }

                    textBackground = R.drawable.background_event_action
                    textRes = R.string.event_action_write_to_organisation
                    clickAction = { onEventClickListener.onActionWriteToOrganization(event) }
                }
                Event.RegistrationStatus.APPROVED -> {
                    textBackground = R.drawable.background_event_action_approved
                    textRes = R.string.event_action_show_event
                    clickAction = { onEventClickListener.onActionShowEvent(event) }
                    textColor = Color.WHITE
                }
                else -> {
                    if (event.isCanRegister()) {
                        textBackground = R.drawable.background_event_action
                        textRes = R.string.event_action_participate
                        clickAction = { onEventClickListener.onActionRegister(event) }
                    } else {
                        isVisible = false
                        return@apply
                    }
                }
            }

            text = resources.getString(textRes)
            setTextColor(textColor)
            background = ContextCompat.getDrawable(context, textBackground)
            isVisible = true
            setOnClickListener { clickAction() }
        }
    }

    override fun getLayout() = R.layout.item_event

    interface OnEventClickListener {
        fun onActionRegister(event: Event)
        fun onActionShowEvent(event: Event)
        fun onActionCancel(event: Event)
        fun onActionWriteToOrganization(event: Event)
        fun onShowEventClick(event: Event)
        fun onShowFilterClick(event: Event)
    }
}