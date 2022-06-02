package com.example.holders.redesign.blocks

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.data.models.EventFormat
import com.example.data.models.EventNew
import com.example.data.models.EventRegistrationStateModel
import com.example.databinding.ItemEventDetailActionBlockBinding
import com.example.extensions.*
import com.example.ui.views.dialogs_new.EventAgreementRegisterDialog
import com.example.util.ClickableSpan
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_form.*
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_no_form.view.*
import setOnClickListener
import java.time.temporal.ChronoUnit
import java.util.*

class EventDetailActionBlock(
    var eventData: EventNew?,
    val clickListener: OnActionClickListener,
) : BindableItem<ItemEventDetailActionBlockBinding>() {

    val status: Event.Status? =
        if (eventData?.status?.value == Event.Status.FINISHED) Event.Status.FINISHED else null
    val userRegistration: Event.Status? = eventData?.binds?.currentUserRegistration?.status?.value
    val backgroundColor: String? = eventData?.binds?.organization?.backgroundColor?.value
    val logo: String? = eventData?.image?.uri

    private var mDate = ""

    init {
        val limitDate = eventData?.requestsApply?.dateLimit
            ?.parseAndFormat(defaultServerDateFormatter, dateFormatterShortDayFullMothShortYear)
        val startRequestDate = eventData?.requestsApply?.dateFrom
            ?.parseAndFormat(defaultServerDateFormatter, dateFormatterShortDayFullMothShortYear)

        if (!eventData?.requestsApply?.dateFrom.isNullOrEmpty()) {
            val mToday = Date()
            val mStartDate = defaultServerDateFormatter.parse(eventData?.requestsApply?.dateFrom)
            //val mDay = ChronoUnit.DAYS.between(mStartDate.toInstant(), mToday.toInstant())

            val mTodayCal = Calendar.getInstance(Locale.getDefault())
            val mStartCal =
                defaultServerDateFormatter.parse(eventData?.requestsApply?.dateFrom).calendar()

            mDate = if (mStartCal.timeInMillis > mTodayCal.timeInMillis) {
                val day = daysBetween(mTodayCal.time, mStartCal.time)
                "До начала приема заявок $day дней"
            } else {
                "Заявки принимаются до $limitDate"
            }
        } else {
            mDate = "Заявки принимаются до $limitDate"
        }

    }

    override fun bind(viewBinding: ItemEventDetailActionBlockBinding, position: Int) {
        viewBinding.apply {

            tvDescription.text = eventData?.description
            tvRequestsDate.text = mDate

            tvAddress.text = showDetailAddress(eventData?.address?.fullValue ?: "")

            if (!eventData?.phone.isNullOrEmpty()) {
                phoneLn.isVisible = true
                tvPhone.text = eventData?.phone?.get(0)?.value
            } else phoneLn.isVisible = false

            if (!eventData?.email.isNullOrEmpty()) {
                emailLn.isVisible = true
                tvEmail.text = eventData?.email?.get(0)?.value
            } else emailLn.isVisible = false

            if (!eventData?.site.isNullOrEmpty()) {
                linkLn.isVisible = true
                tvLink.text = eventData?.site?.get(0)?.value
            } else linkLn.isVisible = false

            if (!eventData?.socialLink.isNullOrEmpty()) {
                networkLn.isVisible = true
                tvSocialNetwork.text = eventData?.socialLink?.get(0)?.value
            } else networkLn.isVisible = false

            decorActionButton(eventData, btnEventAction)
        }
    }

    private fun decorActionButton(eventNew: EventNew?, btnAction: Button) {
        var btnBackground: Int? = null
        @StringRes var btnText: Int? = null
        var clickAction: (() -> Unit)? = null
        var visibility = true
        var mTextSize = 0f

        val userAgreement = eventNew?.userAgreement?.uri
        val eventRegistrationState: EventRegistrationStateModel? =
            eventNew?.binds?.eventRegistrationState

        if (eventRegistrationState != null) {
            val actions = if (eventRegistrationState?.availableActions.isNullOrEmpty())
                arrayListOf("") else eventRegistrationState?.availableActions
            when {
                eventRegistrationState?.prohibitions?.registrationClosed == false -> {
                    when (actions?.get(0)) {
                        "register" -> {
                            mTextSize = 17f
                            btnBackground = R.drawable.custom_btn_white_selectable
                            btnText = R.string.event_action_participate
                            clickAction = {
                                eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                    if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ userAgreement.isNullOrEmpty()) {
                                        clickListener.onActionRegister()
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
                            mTextSize = 17f
                            btnBackground = R.drawable.custom_btn_white_selectable
                            btnText = R.string.event_action_cancel_request
                            clickAction = {
                                eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                    clickListener.onActionCancel()
                                }
                            }
                        }
                        else -> visibility = false
                    }
                }
                else -> {
                    when (actions?.get(0)) {
                        "subscribe" -> {
                            mTextSize = 16f
                            btnBackground = R.drawable.custom_btn_white_selectable
                            if (eventNew.binds?.isUserSubscribed == true) {
                                btnText = R.string.event_action_unsubscribe_request
                                clickAction = {
                                    eventRegistrationState.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                        clickListener.onDeleteSubscribeEvent()
                                    }
                                }
                            } else {
                                btnText = R.string.event_action_subscribe_request
                                clickAction = {
                                    eventRegistrationState.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                        clickListener.onSubscribeEvent()
                                    }
                                }
                            }
                        }
                        else -> {
                            mTextSize = 17f
                            btnBackground = R.drawable.btn_action_background_registration_closed
                            btnText = R.string.about_event_registration_closed
                        }
                    }
                }
            }
        }

        btnAction.apply {
            text = btnText?.let { context.getString(it) }
            setTextColor(Color.BLACK)
            background = btnBackground?.let { ContextCompat.getDrawable(context, it) }
            this.textSize = mTextSize
            if (clickAction != null) {
                setOnClickListener(clickAction)
            } else {
                setOnClickListener(null)
                isEnabled = false
            }

            isVisible = visibility
        }
    }

    private fun showAgreementRegisterDialog(context: Context, url: String) {
        EventAgreementRegisterDialog(context, url).setSelectCallback {
            clickListener.onActionRegister()
        }
    }

    private fun Boolean?.checkStateLevel(hasLevel: () -> Unit) {
        if (this == false) {
            hasLevel()
        } else {
            clickListener.onShowUpdateState()
        }
    }

    private fun showDetailAddress(address: String): String {
        var fullAddress = ""
        if (!address.isNullOrEmpty()) {
            fullAddress = address
        }
        return fullAddress
    }


    override fun bind(
        viewBinding: ItemEventDetailActionBlockBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is EventNew) {
                decorActionButton(payload, viewBinding.btnEventAction)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_action_block

    interface OnActionClickListener {
        fun onActionRegister()
        fun onActionCancel()
        fun onShowUpdateState()
        fun onSubscribeEvent()
        fun onDeleteSubscribeEvent()
    }

    private fun daysBetween(d1: Date, d2: Date): Int {
        return ((d2.time - d1.time) / (1000 * 60 * 60 * 24)).toInt()
    }

}