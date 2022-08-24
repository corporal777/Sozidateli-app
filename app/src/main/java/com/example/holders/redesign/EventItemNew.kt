package com.example.holders.redesign

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.*
import com.example.databinding.ItemEventNewBinding
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.holders.EventStatusItem
import com.example.ui.views.dialogs_new.EventAgreementRegisterDialog
import com.example.util.ClickableSpan
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_form.*
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_no_form.view.*
import parseColor
import setOnClickListener

class EventItemNew(
    val eventData: EventNew?,
    private val onEventClickListener: OnEventClickListener
) : BindableItem<ItemEventNewBinding>(eventData?.id?.toLong() ?: 0) {

    val eventId = eventData?.id.toString()
    val state = eventData?.state
    val status: Event.Status? = eventData?.status?.value
    val userRegistration: Event.Status? = eventData?.binds?.currentUserRegistration?.status?.value
    val backgroundColor: String? = eventData?.binds?.organization?.backgroundColor?.value
    val logo: String? = eventData?.image?.uri

    private val organizationEmails = eventData?.binds?.organization?.email

    private val userAgreement = eventData?.userAgreement?.uri
    private val eventRegistrationState: EventRegistrationStateModel? =
        eventData?.binds?.eventRegistrationState
    private val registrationId = eventData?.binds?.currentUserRegistration?.id.toString()
    private val name: String? = eventData?.name
    private val address = eventData?.address?.getShortAddress()
    val date: String =
        eventData?.holdingDate?.from.formatToEventDatesIntervalOnMain(eventData?.holdingDate?.to)
            ?: ""


    override fun bind(viewBinding: ItemEventNewBinding, position: Int) {
        viewBinding.apply {
            itemContainer.apply {
                //alpha = if (status == Event.Status.FINISHED) 0.4f else 1f
                //clipToOutline = true
            }

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
                val color = backgroundColor.parseColor()
                    ?: ResourcesCompat.getColor(
                        resources,
                        R.color.event_item_no_image_background,
                        null
                    )
                setBackgroundColor(color)
                Picasso.get().load(logo).into(this)
                colorFilter = if (status == Event.Status.CANCELED) ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                else null
            }

            setApproveStatus(tvEventState)
            decorActionButton(btnEventAction)
        }
    }

    private fun setApproveStatus(tvStatus: TextView) {
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

    private fun decorActionButton(btnAction: Button) {
        var btnBackground: Int? = null
        @StringRes var btnText: Int? = null
        var clickAction: (() -> Unit)? = null
        var visibility = true

        if (eventRegistrationState != null) {
            val actions = if (eventRegistrationState?.availableActions.isNullOrEmpty())
                arrayListOf("") else eventRegistrationState?.availableActions
            when {
                eventRegistrationState?.prohibitions?.registrationClosed == false -> {
                    when (actions?.get(0)) {
                        "register" -> {
                            btnBackground = R.drawable.custom_btn_white_selectable
                            btnText = R.string.event_action_participate
                            clickAction = {
                                eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                    if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ userAgreement.isNullOrEmpty()) {
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
                        "withdraw" -> {
                            btnBackground = R.drawable.custom_btn_white_selectable
                            btnText = R.string.event_action_cancel_request
                            clickAction = {
                                eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                    onEventClickListener.onActionCancel(eventId, registrationId)
                                }
                            }
                        }
                        else -> visibility = false
                    }
                }
                else -> {
                    visibility = false
                    // textBackground = R.drawable.btn_action_background_registration_closed
                    //textRes = R.string.about_event_registration_closed
                }
            }
        }

        btnAction.apply {
            text = btnText?.let { context.getString(it) }
            setTextColor(Color.BLACK)
            background = btnBackground?.let { ContextCompat.getDrawable(context, it) }

            if (clickAction != null) {
                setOnClickListener(clickAction)
            } else {
                setOnClickListener(null)
                isEnabled = false
            }

            isVisible = visibility
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

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventStatusItem) return false
        if (eventId != other.eventId) return false
        if (status != other.status) return false
        if (userRegistration != other.userRegistration) return false
        if (backgroundColor != other.backgroundColor) return false
        if (logo != other.logo) return false
        if (organizationEmails != other.organizationEmails) return false

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
                decorActionButton(viewBinding.btnEventAction)
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