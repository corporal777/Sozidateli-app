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
import com.example.ui.views.loading.CustomLoadingButton
import com.example.util.setImage
import com.xwray.groupie.viewbinding.BindableItem

class EventListItem(
    private val eventData: EventNew,
    private val isTemp: Boolean,
    private val clickListener: OnEventClickListener
) : CustomBindingItem<ItemEventNewBinding>(eventData.id?.toLong() ?: 0) {

    private val imageColor =
        ColorDrawable(eventData.backgroundColor?.value.parseColor() ?: Color.DKGRAY)
    private val eventDate = getEventDate()


    override fun bind(viewBinding: ItemEventNewBinding, position: Int) {
        viewBinding.apply {
            itemContainer.setOnClickListener {
                clickListener.onShowEventClick(eventData.id.toString())
            }

            tvDate.text = eventDate
            tvLocation.text = eventData.address?.getShortAddress()
            tvTitle.text = eventData.name
            ivLogo.apply {
                setImage(eventData.image?.uri ?: imageColor, crossfade = 300)
                colorFilter = if (eventData.status?.value != Event.Status.CANCELED) null
                else ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })

            }

            tvEventState.setApproveStatus()
            btnEventAction.setActionButton()
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
                        statusText = R.string.event_closed_request
                    }

                    else -> statusVisibility = false
                }
            }
        }
        text = context.getString(statusText)
        backgroundTintList = ContextCompat.getColorStateList(context, statusBackground)
        isVisible = statusVisibility
    }

    private fun CustomLoadingButton.setActionButton() {
        val registrationState = eventData.binds?.currentUserRegistrationState
        val actions = registrationState?.availableActions ?: arrayListOf("")

        if (isTemp) {
            isVisible = true
            setButtonText(context.getString(R.string.event_action_participate))
            setOnClickListener { clickListener.onShowNeedAuth(eventData.id.toString()) }
        } else if (eventData.isStatusActionAvailable() && !eventData.isRegistrationClosed()) {
            if (actions.contains("register")) {
                isVisible = true
                setButtonText(context.getString(R.string.event_action_participate))
                setOnClickListener {
                    registrationState.checkStateLevel { clickListener.onActionRegister(eventData) }
                }
            } else if (actions.contains("withdraw")) {
                isVisible = true
                setButtonText(context.getString(R.string.event_cancel_request))
                setOnClickListener {
                    registrationState.checkStateLevel { clickListener.onActionCancel(eventData) }
                }
            } else isVisible = false
        } else isVisible = false
    }

    private fun EventRegistrationStateModel?.checkStateLevel(hasLevel: () -> Unit) {
        if (this?.prohibitions?.profileLevelToLow?.value == false) hasLevel()
        else clickListener.onShowUpdateState()
    }



    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is EventListItem) return false
        if (eventData != other.eventData) return false
        return true
    }


    override fun bind(binding: ItemEventNewBinding, payload: Any) {
        if (payload is EventNew) {
            eventData.state?.agreement?.state = payload.state?.agreement?.state
            eventData.binds?.currentUserRegistration = payload.binds?.currentUserRegistration
            eventData.binds?.currentUserRegistrationState = payload.binds?.currentUserRegistrationState
            binding.btnEventAction.setActionButton()
            binding.tvEventState.setApproveStatus()
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

    override fun initializeViewBinding(view: View) = ItemEventNewBinding.bind(view)
    override fun getLayout() = R.layout.item_event_new

    interface OnEventClickListener {
        fun onActionRegister(event: EventNew)
        fun onActionCancel(event: EventNew)
        fun onShowEventClick(eventId: String)
        fun onShowUpdateState()
        fun onShowNeedAuth(eventId: String)
    }
}