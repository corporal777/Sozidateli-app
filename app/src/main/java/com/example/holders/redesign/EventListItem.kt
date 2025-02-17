package com.example.holders.redesign

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemEventNewBinding
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.EventRegistrationStateModel
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.isSameDay
import com.example.extensions.parseColor
import com.example.extensions.parseToDate
import com.example.util.setImage
import com.xwray.groupie.viewbinding.BindableItem

class EventListItem(
    val eventData: EventNew,
    val isTemp: Boolean,
    val clickListener: OnEventClickListener
) : BindableItem<ItemEventNewBinding>(eventData.id?.toLong() ?: 0) {

    private val eventId = eventData.id.toString()
    private val imageColor =
        ColorDrawable(eventData.backgroundColor?.value.parseColor() ?: Color.DKGRAY)
    private val eventDate = getEventDate()


    override fun bind(viewBinding: ItemEventNewBinding, position: Int) {
        viewBinding.apply {
//            cardEvent.setOnClickListener {
//                clickListener.onShowEventClick(viewBinding.root, eventId)
//            }

            tvDate.text = eventDate
            tvLocation.text = eventData.address?.getShortAddress()
            tvTitle.text = eventData.name
            ivLogo.apply {
                setImage(eventData.image?.uri ?: imageColor)
                colorFilter = if (eventData.status?.value == Event.Status.CANCELED)
                    ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                else null
            }

            tvEventState.setApproveStatus()
            //btnEventAction.setActionButton()
        }
    }

    private fun TextView.setApproveStatus() {
        val status = eventData.status?.value
        val userRegistration = eventData.binds?.currentUserRegistration?.status?.value
        var statusBackground = R.color.event_status_finished_background
        var statusText = R.string.event_status_finished
        val statusVisibility: Boolean
        when (status) {
            Event.Status.FINISHED -> {
                statusVisibility = true
                statusBackground = R.color.event_status_finished_background
                statusText = R.string.event_status_finished

            }

            Event.Status.CANCELED -> {
                statusVisibility = true
                statusBackground = R.color.event_status_cancelled_background
                statusText = R.string.event_status_cancelled
            }

            else -> {
                when (userRegistration) {
                    Event.Status.APPROVED -> {
                        statusVisibility = true
                        statusBackground = R.color.event_status_approved_background
                        statusText = R.string.event_status_approved_new
                    }

                    Event.Status.PENDING -> {
                        statusVisibility = true
                        statusBackground = R.color.event_status_wait_confirmation_background
                        statusText = R.string.event_status_wait_confirmation
                    }

                    Event.Status.DECLINED -> {
                        statusVisibility = true
                        statusBackground = R.color.event_status_declined_background
                        statusText = R.string.event_status_decline_new
                    }

                    Event.Status.REGISTRATION_FINISHED -> {
                        statusVisibility = true
                        statusBackground = R.color.event_status_wait_confirmation_background
                        statusText = R.string.event_action_closed_request
                    }

                    else -> statusVisibility = false
                }
            }
        }
        text = context.getString(statusText)
        backgroundTintList = ContextCompat.getColorStateList(context, statusBackground)
        isVisible = statusVisibility
    }

    private fun Button.setActionButton() {
        val registrationId = eventData.binds?.currentUserRegistration?.id.toString()
        val registrationState = eventData.binds?.currentUserRegistrationState
        val userAgreement = eventData.userAgreement?.uri
        val actions = registrationState?.availableActions ?: arrayListOf("")

        if (isTemp) {
            isVisible = true
            text = context.getString(R.string.event_action_participate)
            setOnClickListener { clickListener.onShowNeedAuth(eventId) }
        } else if (eventData.isStatusActionAvailable() && !eventData.isRegistrationClosed()) {
            if (actions.contains("register")) {
                isVisible = true
                text = context.getString(R.string.event_action_participate)
                setOnClickListener {
                    registrationState.checkStateLevel {
                        clickListener.onActionRegister(
                            eventId,
                            userAgreement,
                            eventData.isFormEnabled()
                        )
                    }
                }
            } else if (actions.contains("withdraw")) {
                isVisible = true
                text = context.getString(R.string.event_action_cancel_request)
                setOnClickListener {
                    registrationState.checkStateLevel {
                        clickListener.onActionCancel(eventId, registrationId)
                    }
                }
            } else isVisible = false
        } else isVisible = false
    }

    private fun EventRegistrationStateModel?.checkStateLevel(hasLevel: () -> Unit) {
        if (this?.prohibitions?.profileLevelToLow?.value == false) hasLevel()
        else clickListener.onShowUpdateState()
    }

    override fun initializeViewBinding(view: View) = ItemEventNewBinding.bind(view)
    override fun getLayout() = R.layout.item_event_new

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is EventListItem) return false
        if (eventData != other.eventData) return false
        return true
    }

    override fun bind(
        viewBinding: ItemEventNewBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is EventNew) {
                eventData.binds?.currentUserRegistration = payload.binds?.currentUserRegistration
                eventData.binds?.currentUserRegistrationState = payload.binds?.currentUserRegistrationState
                //viewBinding.btnEventAction.setActionButton()
                viewBinding.tvEventState.setApproveStatus()
            }
        }

    }

    private fun getEventDate(): String? {
        val dateStart = eventData.holdingDate?.from ?: return null
        val dateEnd = eventData.holdingDate?.to ?: return null

        val startDate =
            dateStart.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null
        val finishDate =
            dateEnd.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null

        return if (startDate.isSameDay(finishDate)) dateStart.formatToDefaultDate()
        else dateStart.formatToDefaultDate() + " - " + dateEnd.formatToDefaultDate()
    }

    interface OnEventClickListener {
        fun onActionRegister(event: String, agreementUrl: String?, formEnabled: Boolean)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(view: View, event: String)
        fun onShowUpdateState()
        fun onShowNeedAuth(eventId: String)
    }
}