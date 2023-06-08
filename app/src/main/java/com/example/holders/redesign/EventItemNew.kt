package com.example.holders.redesign

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.EventRegistrationStateModel
import com.example.data.models.EventStateModel
import com.example.databinding.ItemEventNewBinding
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.ui.views.dialogs_new.EventAgreementRegisterDialog
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import parseColor

class EventItemNew(
    eventData: EventNew?,
    val eventId: String,
    val state: EventStateModel?,
    val status: Event.Status?,
    val userRegistration: Event.Status?,
    val backgroundColor: String?,
    val logo: String?,
    val eventRegistrationState: EventRegistrationStateModel?,
    val userAgreement: String?,
    val registrationId: String,
    val name: String?,
    val address: String?,
    val dateFrom: String?,
    val dateTo: String?,
    private val clickListener: OnEventClickListener
) : BindableItem<ItemEventNewBinding>(eventData?.id?.toLong() ?: 0) {

    private val date = dateFrom.formatToEventDatesIntervalOnMain(dateTo) ?: ""
    private val imageColor = ColorDrawable(backgroundColor.parseColor() ?: Color.DKGRAY)

    init {

    }

    override fun bind(viewBinding: ItemEventNewBinding, position: Int) {
        viewBinding.apply {
            cardEvent.setOnClickListener {
                clickListener.onShowEventClick(viewBinding.root, eventId)
            }

            tvDate.text = date
            tvLocation.text = address
            tvTitle.text = name
            ivLogo.apply {
                setImage(logo ?: imageColor)
                colorFilter = if (status == Event.Status.CANCELED)
                    ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                else null
            }

            setApproveStatus(tvEventState, status, userRegistration)
            decorActionButton(btnEventAction, status, eventRegistrationState)
        }
    }

    private fun setApproveStatus(
        tvStatus: TextView,
        status: Event.Status?,
        userRegistration: Event.Status?
    ) {
        var statusBackground = R.color.event_status_finished_background
        var statusText = R.string.event_status_finished
        var statusVisibility = false
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
                        statusText = R.string.about_event_registration_closed
                    }
                    else -> statusVisibility = false
                }
            }
        }
        tvStatus.apply {
            text = context.getString(statusText)
            backgroundTintList = ContextCompat.getColorStateList(context, statusBackground)
            isVisible = statusVisibility
        }
    }

    private fun decorActionButton(
        btnAction: Button,
        status: Event.Status?,
        eventRegistrationState: EventRegistrationStateModel?
    ) {
        when (status) {
            Event.Status.REGISTRATION,
            Event.Status.REGISTRATION_FINISHED,
            Event.Status.RUNNING,
            Event.Status.FINISHED,
            Event.Status.APPROVED -> {
                if (eventRegistrationState != null) {
                    val actions = eventRegistrationState.availableActions ?: arrayListOf("")
                    val profileLevel = eventRegistrationState.prohibitions?.profileLevelToLow?.value
                    if (eventRegistrationState.prohibitions?.registrationClosed == false) {
                        if (actions.firstOrNull() == "register") {
                            btnAction.apply {
                                isVisible = true
                                text = context.getString(R.string.event_action_participate)
                                setOnClickListener {
                                    profileLevel.checkStateLevel {
                                        clickListener.onActionRegister(eventId, userAgreement)
                                    }
                                }
                            }
                        } else if (actions.firstOrNull() == "withdraw") {
                            btnAction.apply {
                                isVisible = true
                                text = context.getString(R.string.event_action_cancel_request)
                                setOnClickListener {
                                    profileLevel.checkStateLevel {
                                        clickListener.onActionCancel(eventId, registrationId)
                                    }
                                }
                            }
                        } else btnAction.isVisible = false
                    } else btnAction.isVisible = false
                } else btnAction.isVisible = false
            }
            else -> btnAction.isVisible = false
        }
    }

    private fun Boolean?.checkStateLevel(hasLevel: () -> Unit) {
        if (this == false) hasLevel()
        else clickListener.onShowUpdateState()
    }


    override fun getLayout() = R.layout.item_event_new

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventItemNew) return false
        if (eventId != other.eventId) return false
        if (state != other.state) return false
        if (status != other.status) return false
        if (userRegistration != other.userRegistration) return false
        if (backgroundColor != other.backgroundColor) return false
        if (logo != other.logo) return false
        if (eventRegistrationState != other.eventRegistrationState) return false
        if (userAgreement != other.userAgreement) return false
        if (registrationId != other.registrationId) return false
        if (name != other.name) return false
        if (address != other.address) return false
        if (dateFrom != other.dateFrom) return false
        if (dateTo != other.dateTo) return false
        return true
    }

    override fun bind(
        viewBinding: ItemEventNewBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is EventNew) {
                decorActionButton(
                    viewBinding.btnEventAction,
                    payload.status?.value,
                    payload.binds?.eventRegistrationState
                )
                setApproveStatus(
                    viewBinding.tvEventState,
                    payload.status?.value,
                    payload.binds?.currentUserRegistration?.status?.value
                )
            }
        }

    }

    interface OnEventClickListener {
        fun onActionRegister(event: String, agreementUrl: String?)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(view: View, event: String)
        fun onShowUpdateState()
    }
}