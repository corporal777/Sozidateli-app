package com.example.holders

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.BuildConfig
import com.example.R
import com.example.data.models.*
import com.example.util.ClickableSpan
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_form.*
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_no_form.view.*
import kotlinx.android.synthetic.main.item_event_status.*
import parseColor
import setOnClickListener

class EventStatusItem(
        itemId: Long,
        val eventId: String,
        val status: Event.Status?,
        val userRegistration: Event.Status?,
        val backgroundColor: String?,
        val logo: String?,
        val format: EventFormat?,
        val organizationEmails: List<EventPhoneModel/*EmailAffiliation*/>?,
        private val conferenceRegistrationClosed: Boolean,
        private val onEventClickListener: OnEventClickListener,
        private val userAgreement: String?,
        private val canShowActionButton: Boolean = true,
        private val eventRegistrationState: EventRegistrationStateModel? = null,
        private val registrationId: String? = null
) : Item(itemId) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemContainer.apply {
                alpha = if (status == Event.Status.FINISHED) 0.4f else 1f
                clipToOutline = true
                setOnClickListener { onEventClickListener.onShowEventClick(itemView, eventId) }
            }

            ivLogo.apply {
                val color = backgroundColor.parseColor()
                        ?: ResourcesCompat.getColor(resources, R.color.event_item_no_image_background, null)
                setBackgroundColor(color)
                Picasso.get().load(logo).into(this)
            }

            // отключили клик из-за кастомных форматов - карточка #2319
            tvEventFormat.apply {
                if (format == null) {
                    isVisible = false
                } else {
                    isVisible = true
                    text = format.name
//                    setOnClickListener { onEventClickListener.onShowFilterClick(format.id) }
                }

                isClickable = false
                isFocusable = false
            }

            setApproveStatus(tvStatus)

            tvFinished.isVisible = status == Event.Status.FINISHED

            decorActionButton(btnEventAction)
        }
    }

    private fun setApproveStatus(tvStatus: TextView) {
        tvStatus.apply {
            val textBackground: Int
            val textRes: Int
            when (userRegistration) {
                Event.Status.APPROVED -> {
                    textBackground = R.color.event_status_approved_background
                    textRes = R.string.event_status_approved
                }
                Event.Status.PENDING -> {
                    textBackground = R.color.event_status_wait_confirmation_background
                    textRes = R.string.event_status_wait_confirmation
                }
                Event.Status.DECLINED -> {
                    textBackground = R.color.event_status_declined_background
                    textRes = R.string.event_status_decline
                }
//                Event.Status.REGISTRATION -> {
//                    textBackground = R.color.event_status_wait_confirmation_background
//                    textRes = R.string.event_action_was_sent_request
//                }
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

    private fun decorActionButton(btnAction: Button) {
        var textBackground: Int? = null
        var textColor = Color.BLACK
        @StringRes var textRes: Int? = null
        var clickAction: (() -> Unit)? = null
        var visibility = true

        if (eventRegistrationState != null) {
            val actions = if (eventRegistrationState?.availableActions.isNullOrEmpty())
                arrayListOf("") else eventRegistrationState?.availableActions
            when {
                eventRegistrationState?.prohibitions?.registrationClosed == false -> {
                    when (actions?.get(0)) {
                        "register" -> {
                            textBackground = R.drawable.background_event_action
                            textRes = R.string.event_action_participate
                            clickAction = {
                                eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                    if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ userAgreement.isNullOrEmpty()) {
                                        onEventClickListener.onActionRegister(eventId)
                                    } else {
                                        showAgreementRegisterDialog(btnAction.context, userAgreement)
                                    }
                                }
                            }
                        }
                        "withdraw" -> {
                            textBackground = R.drawable.background_event_action
                            textRes = R.string.event_action_cancel_request
                            clickAction = {
                                eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                    onEventClickListener.onActionCancel(eventId, registrationId)
                                }
                            }
                        }
                        "view" -> {
                            textBackground = R.drawable.background_event_action_approved
                            textRes = R.string.event_action_show_event
                            clickAction = {
                                eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                    onEventClickListener.onActionShowEvent(eventId)
                                }
                            }
                            textColor = Color.WHITE
                        }
                    }
                }
                else -> {
                    if (actions?.get(0) ?: "" == "view") {
                        textBackground = R.drawable.background_event_action_approved
                        textRes = R.string.event_action_show_event
                        clickAction = {
                            eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                onEventClickListener.onActionShowEvent(eventId)
                            }
                        }
                        textColor = Color.WHITE
                    } else {
                        textBackground = R.drawable.background_event_action_disabled
                        textRes = R.string.about_event_registration_closed
                    }
                }
            }
        }
        /*when {
            !canShowActionButton ||
                    status == null ||
                    status == Event.Status.FINISHED -> {
                visibility = false
            }
            conferenceRegistrationClosed &&
                    (userRegistration != Event.Status.APPROVED ||
                            userRegistration != Event.Status.PENDING) -> {
                textBackground = R.drawable.background_event_action_disabled
                textRes = R.string.about_event_registration_closed
            }
            else -> when (userRegistration) {
                Event.Status.APPROVED -> {
                    textBackground = R.drawable.background_event_action_approved
                    textRes = R.string.event_action_show_event
                    clickAction = { onEventClickListener.onActionShowEvent(eventId) }
                    textColor = Color.WHITE
                }
                Event.Status.PENDING -> {
                    textBackground = R.drawable.background_event_action
                    textRes = R.string.event_action_cancel_request
                    clickAction = { onEventClickListener.onActionCancel(eventId, registrationId) }
                }
                Event.Status.DECLINED -> {
                    if (BuildConfig.NEW_PROFILE_EDIT) {
                        visibility = organizationEmails.isNullOrEmpty().not()
                        textBackground = R.drawable.background_event_action
                        textRes = R.string.event_action_write_to_organisation
                        clickAction = {
                            onEventClickListener.onActionWriteToOrganization(organizationEmails ?: emptyList())
                        }
                    }
                }
                else -> {
                    textBackground = R.drawable.background_event_action
                    textRes = R.string.event_action_participate
                    clickAction = {
                        if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ userAgreement.isNullOrEmpty()) {
                            onEventClickListener.onActionRegister(eventId)
                        } else {
                            showAgreementRegisterDialog(btnAction.context, userAgreement)
                        }
                    }
                }
            }
        }*/

        btnAction.apply {
            text = textRes?.let { context.getString(it) }
            setTextColor(textColor)
            background = textBackground?.let { ContextCompat.getDrawable(context, it) }

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
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_event_registration_agreement_form, null).apply {
            val agreementText = SpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
                val linkStart = 11
                val linkEnd = length
                setSpan(ClickableSpan(drawUnderline = false) {
                    showUserAgreement(context, url)
                }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }

            tvAgree.apply {
                text = agreementText
                movementMethod = LinkMovementMethod.getInstance()
            }

            cbAgree.setOnCheckedChangeListener { _, checked ->
                btnPositive.isEnabled = checked
            }
        }

        AlertDialog.Builder(context)
                .setView(view)
                .create()
                .apply {
                    setOnShowListener {
                        view.apply {
                            btnPositive.apply {
                                isEnabled = false
                                setOnClickListener {
                                    onEventClickListener.onActionRegister(eventId)
                                    dismiss()
                                }
                            }

                            btnNegative.setOnClickListener {
                                dismiss()
                            }
                        }
                    }
                }
                .show()
    }

    private fun showUserAgreement(context: Context, url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(context, R.string.about_event_agreement_open_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun getLayout() = R.layout.item_event_status

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is EventStatusItem) return false

        if (eventId != other.eventId) return false
        if (status != other.status) return false
        if (userRegistration != other.userRegistration) return false
        if (backgroundColor != other.backgroundColor) return false
        if (logo != other.logo) return false
        if (format != other.format) return false
        if (organizationEmails != other.organizationEmails) return false

        return true
    }

    interface OnEventClickListener {
        fun onActionRegister(event: String)
        fun onActionShowEvent(event: String)
        fun onActionCancel(event: String, registrationId: String?)
        fun onActionWriteToOrganization(emails: List<EventPhoneModel/*EmailAffiliation*/>)
        fun onShowEventClick(view: View, event: String)
        fun onShowFilterClick(format: Int)
        fun onShowUpdateState()
    }
}