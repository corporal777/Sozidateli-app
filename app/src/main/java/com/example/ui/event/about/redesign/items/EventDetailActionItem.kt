package com.example.ui.event.about.redesign.items

import android.content.Context
import android.graphics.Color
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.data.models.EventFormat
import com.example.data.models.EventNew
import com.example.data.models.EventRegistrationStateModel
import com.example.databinding.ItemEventDetailActionBlockBinding
import com.example.extensions.dateFormatterShortDayFullMothShortYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseAndFormat
import com.example.ui.views.dialogs_new.EventAgreementRegisterDialog
import com.example.util.markWon
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener
import java.util.*


class EventDetailActionItem(
    val eventData: EventNew?,
    val clickListener: OnActionClickListener,
) : BindableItem<ItemEventDetailActionBlockBinding>() {

    val status: Event.Status? =
        if (eventData?.status?.value == Event.Status.FINISHED) Event.Status.FINISHED else null
    val userRegistration: Event.Status? = eventData?.binds?.currentUserRegistration?.status?.value
    val backgroundColor: String? = eventData?.binds?.organization?.backgroundColor?.value
    val logo: String? = eventData?.image?.uri
    val mFormat = EventFormat(
        name = if (eventData?.format?.name.isNullOrEmpty()) eventData?.format?.custom
            ?: "" else eventData?.format?.name ?: ""
    )

    private var mDate = ""
    private var mCanShowDate = false

    init {
        val limitDate = eventData?.requestsApply?.dateLimit
            ?.parseAndFormat(defaultServerDateFormatter, dateFormatterShortDayFullMothShortYear)

        val requestDate = eventData?.requestsApply?.dateFrom
        if (!requestDate.isNullOrEmpty()) {
            val mToday = System.currentTimeMillis()
            val mStartReq =
                defaultServerDateTimeFormatter.parse(eventData?.requestsApply?.dateFrom).time

            if (mStartReq > mToday) {
                val day = daysBetweenNew(mToday, mStartReq)
                mCanShowDate = true
                mDate = "До начала приема заявок $day дней"
            } else {
                mCanShowDate = !limitDate.isNullOrEmpty()
                mDate = "Заявки принимаются по $limitDate"
            }
        } else {
            mCanShowDate = !limitDate.isNullOrEmpty()
            mDate = "Заявки принимаются по $limitDate"
        }
    }

    override fun bind(viewBinding: ItemEventDetailActionBlockBinding, position: Int) {
        viewBinding.apply {

            if (mCanShowDate) {
                tvRequestsDate.apply {
                    isVisible = true
                    tvRequestsDate.text = mDate
                }
            }
            //tvDescription.text = eventData?.description

            markWon(viewBinding.root.context).setMarkdown(
                tvDescription,
                eventData?.description ?: ""
            );

            if (!mFormat.name.isNullOrEmpty()) {
                formatLn.isVisible = true
                tvFormat.text = mFormat.name
            } else formatLn.isVisible = false
            if (!eventData?.address?.fullValue.isNullOrEmpty()) {
                addressLn.isVisible = true
                tvAddress.text = eventData?.address?.fullValue
            } else addressLn.isVisible = false
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
            when (eventRegistrationState?.prohibitions?.registrationClosed) {
                false -> {
                    when (actions?.get(0)) {
                        "register" -> {
                            mTextSize = 17f
                            btnBackground = R.drawable.custom_btn_white_ghost_selectable
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
                            btnBackground = R.drawable.custom_btn_white_ghost_selectable
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
                            btnBackground = R.drawable.custom_btn_white_ghost_selectable
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

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventDetailActionItem) return false
        if (eventData != other.eventData) return false
        return true
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

    private fun daysBetweenNew(d1: Long, d2: Long): Int {
        var days = 0
        for (i in d1..d2 step 86400000) {
            days++
        }
        return days
    }

}