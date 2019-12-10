package com.example.holders

import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.data.models.EventFormat
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_status.*
import parseColor

class EventStatusItem(
        itemId: Long,
        private val eventId: String,
        private val status: Event.Status?,
        private val userRegistration: Event.RegistrationStatus?,
        private val backgroundColor: String?,
        private val logo: String?,
        private val format: EventFormat?,
        private val organizationEmails: List<EmailAffiliation>?,
        private val onEventClickListener: OnEventClickListener
) : Item(itemId) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemContainer.apply {
                alpha = if (status == Event.Status.CONFERENCE_ENDS) 0.4f else 1f
                clipToOutline = true
                setOnClickListener { onEventClickListener.onShowEventClick(eventId) }
            }

            ivLogo.apply {
                val color = backgroundColor.parseColor()
                        ?: ResourcesCompat.getColor(resources, R.color.event_item_no_image_background, null)
                setBackgroundColor(color)
                Picasso.get().load(logo).into(this)
            }

            tvEventFormat.apply {
                if (format == null) {
                    isVisible = false
                } else {
                    isVisible = true
                    text = format.name
                    setOnClickListener { onEventClickListener.onShowFilterClick(format.id) }
                }
            }

            setApproveStatus(tvStatus)

            tvFinished.isVisible = status == Event.Status.CONFERENCE_ENDS

            setAction(btnEventAction)
        }
    }

    private fun setApproveStatus(tvStatus: TextView) {
        tvStatus.apply {
            val textBackground: Int
            val textRes: Int
            when (userRegistration) {
                Event.RegistrationStatus.APPROVED -> {
                    textBackground = R.color.event_status_approved_background
                    textRes = R.string.event_status_approved
                }
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
            if (status == null || status == Event.Status.CONFERENCE_ENDS) {
                isVisible = false
                return@apply
            } else when (userRegistration) {
                Event.RegistrationStatus.PENDING -> {
                    textBackground = R.drawable.background_event_action
                    textRes = R.string.event_action_cancel_request
                    clickAction = { onEventClickListener.onActionCancel(eventId) }
                }
                Event.RegistrationStatus.DECLINED -> {
                    if (organizationEmails.isNullOrEmpty()) {
                        isVisible = false
                        return@apply
                    }

                    textBackground = R.drawable.background_event_action
                    textRes = R.string.event_action_write_to_organisation
                    clickAction = { onEventClickListener.onActionWriteToOrganization(organizationEmails) }
                }
                Event.RegistrationStatus.APPROVED -> {
                    textBackground = R.drawable.background_event_action_approved
                    textRes = R.string.event_action_show_event
                    clickAction = { onEventClickListener.onActionShowEvent(eventId) }
                    textColor = Color.WHITE
                }
                else -> {
                    if (Event.isCanRegister(status, userRegistration)) {
                        textBackground = R.drawable.background_event_action
                        textRes = R.string.event_action_participate
                        clickAction = { onEventClickListener.onActionRegister(eventId) }
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

    override fun getLayout() = R.layout.item_event_status

    interface OnEventClickListener {
        fun onActionRegister(event: String)
        fun onActionShowEvent(event: String)
        fun onActionCancel(event: String)
        fun onActionWriteToOrganization(emails: List<EmailAffiliation>)
        fun onShowEventClick(event: String)
        fun onShowFilterClick(format: Int)
    }
}