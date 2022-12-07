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
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.*
import com.example.databinding.ItemEventNewBinding
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.holders.EventStatusItem
import com.example.ui.views.dialogs_new.EventAgreementRegisterDialog
import com.example.util.setImage
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import parseColor
import setOnClickListener

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
    dateFrom: String?,
    dateTo: String?,
    private val onEventClickListener: OnEventClickListener
) : BindableItem<ItemEventNewBinding>(eventData?.id?.toLong() ?: 0) {

    val date = dateFrom.formatToEventDatesIntervalOnMain(dateTo) ?: ""
    private var imageColor = ColorDrawable(Color.DKGRAY)

    init {
        if (!backgroundColor.isNullOrEmpty()) {
            val color = backgroundColor.parseColor() ?: Color.DKGRAY
            imageColor = ColorDrawable(color)
        }
    }

    override fun bind(viewBinding: ItemEventNewBinding, position: Int) {
        viewBinding.apply {
            cardEvent.setOnClickListener {
                onEventClickListener.onShowEventClick(
                    viewBinding.root,
                    eventId
                )
            }

            tvDate.text = date
            tvLocation.text = address
            tvTitle.text = name
            ivLogo.apply {
                setImage(logo ?: imageColor)
                colorFilter =
                    if (status == Event.Status.CANCELED) ColorMatrixColorFilter(ColorMatrix().apply {
                        setSaturation(0f)
                    })
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
        tvStatus.apply {
            val mTextBackground: Int
            val mTextRes: Int
            when (status) {
                Event.Status.FINISHED -> {
                    mTextBackground = R.color.event_status_finished_background
                    mTextRes = R.string.event_status_finished
                }
                Event.Status.CANCELED -> {
                    mTextBackground = R.color.event_status_cancelled_background
                    mTextRes = R.string.event_status_cancelled
                }
                else -> {
                    when (userRegistration) {
                        Event.Status.APPROVED -> {
                            mTextBackground = R.color.event_status_approved_background
                            mTextRes = R.string.event_status_approved_new
                        }
                        Event.Status.PENDING -> {
                            mTextBackground = R.color.event_status_wait_confirmation_background
                            mTextRes = R.string.event_status_wait_confirmation
                        }
                        Event.Status.DECLINED -> {
                            mTextBackground = R.color.event_status_declined_background
                            mTextRes = R.string.event_status_decline_new
                        }
                        Event.Status.REGISTRATION_FINISHED -> {
                            mTextBackground = R.color.event_status_wait_confirmation_background
                            mTextRes = R.string.about_event_registration_closed
                        }
                        else -> {
                            isVisible = false
                            return
                        }
                    }
                }
            }

            text = resources.getString(mTextRes)
            backgroundTintList = ContextCompat.getColorStateList(context, mTextBackground)
            isVisible = true
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
            Event.Status.APPROVED-> {
                if (eventRegistrationState != null) {
                    val actions = eventRegistrationState.availableActions ?: arrayListOf("")
                    val profileLevel = eventRegistrationState.prohibitions?.profileLevelToLow?.value
                    if (eventRegistrationState.prohibitions?.registrationClosed == false) {
                        when (actions.firstOrNull()) {
                            "register" -> {
                                btnAction.apply {
                                    isVisible = true
                                    text = context.getString(R.string.event_action_participate)
                                    setOnClickListener {
                                        profileLevel.checkStateLevel {
                                            if (userAgreement.isNullOrEmpty()) {
                                                onEventClickListener.onActionRegister(eventId)
                                            } else {
                                                showAgreementRegisterDialog(
                                                    btnAction.context,
                                                    userAgreement
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            "withdraw" -> {
                                btnAction.apply {
                                    isVisible = true
                                    text = context.getString(R.string.event_action_cancel_request)
                                    setOnClickListener {
                                        profileLevel.checkStateLevel {
                                            onEventClickListener.onActionCancel(
                                                eventId,
                                                registrationId
                                            )
                                        }
                                    }
                                }

                            }
                            else -> btnAction.isVisible = false
                        }
                    } else btnAction.isVisible = false
                }
            }
            else -> btnAction.isVisible = false
        }
    }

    private fun Boolean?.checkStateLevel(hasLevel: () -> Unit) {
        if (this == false) {
            hasLevel()
        } else {
            onEventClickListener.onShowUpdateState()
        }
    }

    private fun showAgreementRegisterDialog(context: Context, url: String) {
        EventAgreementRegisterDialog(context, url).setSelectCallback {
            onEventClickListener.onActionRegister(eventId)
        }
    }

    override fun getLayout() = R.layout.item_event_new

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is EventItemNew) return false
        if (eventId != other.eventId) return false
        if (status != other.status) return false
        if (userRegistration != other.userRegistration) return false
        if (backgroundColor != other.backgroundColor) return false
        if (logo != other.logo) return false
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
        fun onActionRegister(event: String)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(view: View, event: String)
        fun onShowUpdateState()
    }
}