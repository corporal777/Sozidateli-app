package com.example.holders.redesign.blocks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.data.models.EventFormat
import com.example.data.models.EventNew
import com.example.databinding.ItemEventDetailActionBlockBinding
import com.example.extensions.dateFormatterShortDayFullMothShortYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.extensions.parseAndFormat
import com.example.util.ClickableSpan
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_form.*
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_no_form.view.*

class EventDetailActionBlock(
    val eventNew: EventNew?,
    val clickListener: OnEventClickListener,
    private val onRegisterClick: () -> Unit,
    private val onCancelRegisterClick: () -> Unit,
    private val onShowUpdateState: () -> Unit,
) : BindableItem<ItemEventDetailActionBlockBinding>() {

    private var btnAction: Button? = null
    private var eventData = eventNew
    val status: Event.Status? =
        if (eventData?.status?.value == Event.Status.FINISHED) Event.Status.FINISHED else null
    val userRegistration: Event.Status? = eventData?.binds?.currentUserRegistration?.status?.value
    val backgroundColor: String? = eventData?.binds?.organization?.backgroundColor?.value
    val logo: String? = eventData?.image?.uri
    val format: EventFormat? = EventFormat(
        name = if (eventData?.format?.name.isNullOrEmpty()) eventData?.format?.custom
            ?: "" else eventData?.format?.name ?: ""
    )

    val date: String =
        eventData?.holdingDate?.from.formatToEventDatesIntervalOnMain(eventData?.holdingDate?.to)
            ?: ""

    private val limitDate = eventData?.requestsApply?.dateLimit
        ?.parseAndFormat(defaultServerDateFormatter, dateFormatterShortDayFullMothShortYear)

    override fun bind(viewBinding: ItemEventDetailActionBlockBinding, position: Int) {
        viewBinding.apply {

            btnAction = btnEventAction

            tvDescription.text = eventData?.description
            tvRequestsDate.text = "Заявки принимаются до $limitDate"

            tvAddress.text = showDetailAddress(eventData?.address?.fullValue ?: "")

            if (!eventData?.phone.isNullOrEmpty()) {
                phoneLn.isVisible = true
                tvPhone.text = eventData?.phone?.get(0)?.value
            }

            if (!eventData?.email.isNullOrEmpty()) {
                emailLn.isVisible = true
                tvEmail.text = eventData?.email?.get(0)?.value
            }
            if (!eventData?.site.isNullOrEmpty()) {
                linkLn.isVisible = true
                tvLink.text = eventData?.site?.get(0)?.value
            }
            if (!eventData?.socialLink.isNullOrEmpty()) {
                networkLn.isVisible = true
                tvSocialNetwork.text = eventData?.socialLink?.get(0)?.value
            }

            decorActionButton(eventData, btnAction as AppCompatButton)
        }
    }

    private fun decorActionButton(eventData: EventNew?, btnAction: Button) {

        val eventRegistrationState = eventData?.binds?.eventRegistrationState
        val userAgreement: String? =
            eventData?.userAgreement?.name ?: eventData?.userAgreement?.uri

        if (eventRegistrationState != null) {
            val actions = if (eventRegistrationState?.availableActions.isNullOrEmpty())
                arrayListOf("") else eventRegistrationState?.availableActions
            when {
                eventRegistrationState?.prohibitions?.registrationClosed == false -> {
                    when (actions?.get(0)) {
                        "register" -> {
                            Log.e("STATE", actions?.get(0))
                            btnAction.apply {
                                setText(R.string.event_action_participate)
                                setOnClickListener {
                                    eventRegistrationState!!.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                        if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ userAgreement.isNullOrEmpty()) {
                                            clickListener?.onActionRegister()
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
                                setText(R.string.event_action_cancel_request)
                                setOnClickListener {
                                    eventRegistrationState!!.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                        clickListener?.onActionCancel()
                                    }
                                }
                            }
                        }
                        "view" -> {
                            btnAction.apply {
                                setText(R.string.event_action_show_event)
                                setOnClickListener {
                                    eventRegistrationState!!.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                        //onEventClickListener.onActionShowEvent(eventId)
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    if (actions?.get(0) ?: "" == "view") {
                        btnAction.apply {
                            setText(R.string.event_action_show_event)
                            setOnClickListener {
                                eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                }
                            }
                        }
                    } else {
                        btnAction.apply {
                            setText(R.string.about_event_registration_closed)
                            isEnabled = false
                        }
                    }
                }
            }
        }
    }

    private fun showAgreementRegisterDialog(context: Context, url: String) {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_event_registration_agreement_form, null).apply {
                val agreementText =
                    SpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
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
                                clickListener?.onActionRegister()
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
            Toast.makeText(context, R.string.about_event_agreement_open_error, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun Boolean?.checkStateLevel(hasLevel: () -> Unit) {
        if (this == false) {
            hasLevel()
        } else {
            onShowUpdateState
        }
    }

    private fun showDetailAddress(address: String): String {
        var fullAddress = ""
        if (!address.isNullOrEmpty()) {
            fullAddress = address
        }
        return fullAddress
    }

    fun updateButtonActionState(eventNew: EventNew?) {
        eventData = eventNew
        decorActionButton(eventNew, btnAction!!)
    }


    override fun getLayout(): Int = R.layout.item_event_detail_action_block

    interface OnEventClickListener {
        fun onActionRegister()
        fun onActionCancel()
        fun onShowFilterClick(format: Int)
        fun onShowUpdateState()
    }

}