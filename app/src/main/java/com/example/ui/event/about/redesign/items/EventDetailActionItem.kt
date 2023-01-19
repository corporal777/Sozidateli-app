package com.example.ui.event.about.redesign.items

import android.content.Context
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.data.models.EventFormat
import com.example.data.models.EventNew
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
) : BindableItem<ItemEventDetailActionBlockBinding>(-1001L) {

    val status: Event.Status? = eventData?.status?.value
    val userRegistration: Event.Status? = eventData?.binds?.currentUserRegistration?.status?.value
    val backgroundColor: String? = eventData?.binds?.organization?.backgroundColor?.value
    val logo: String? = eventData?.image?.uri

    val eventFormat = EventFormat(
        name = if (!eventData?.binds?.format?.name.isNullOrEmpty()) {
            eventData?.binds?.format?.name ?: ""
        } else {
            eventData?.format?.name ?: ""
        }
    )

    private var eventDate = ""
    private var canShowDate = false
    private var viewHeight = 0

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
                canShowDate = true
                eventDate = if (day == 1) {
                    "До начала приема заявок $day день"
                } else if (day != 1 && day < 5) {
                    "До начала приема заявок $day дня"
                } else {
                    "До начала приема заявок $day дней"
                }
            } else {
                canShowDate = !limitDate.isNullOrEmpty()
                eventDate = "Заявки принимаются по $limitDate"
            }
        } else {
            canShowDate = !limitDate.isNullOrEmpty()
            eventDate = "Заявки принимаются по $limitDate"
        }
    }

    override fun bind(viewBinding: ItemEventDetailActionBlockBinding, position: Int) {
        viewBinding.apply {
            tvRequestsDate.apply {
                isInvisible = !canShowDate
                tvRequestsDate.text = eventDate
            }
            //tvDescription.text = eventData?.description

            markWon(viewBinding.root.context).setMarkdown(
                tvDescription,
                eventData?.description ?: ""
            )

            formatLn.isVisible = !eventFormat.name.isNullOrEmpty()
            tvFormat.text = eventFormat.name

            addressLn.isVisible = !eventData?.address?.fullValue.isNullOrEmpty()
            tvAddress.text = eventData?.address?.fullValue

            phoneLn.isVisible = !eventData?.phone.isNullOrEmpty()
            tvPhone.text = eventData?.phone?.firstOrNull()?.value

            emailLn.isVisible = !eventData?.email.isNullOrEmpty()
            tvEmail.text = eventData?.email?.firstOrNull()?.value

            linkLn.isVisible = !eventData?.site.isNullOrEmpty()
            tvLink.text = eventData?.site?.firstOrNull()?.value

            networkLn.isVisible = !eventData?.socialLink.isNullOrEmpty()
            tvSocialNetwork.text = eventData?.socialLink?.firstOrNull()?.value

            decorActionButton(eventData, btnEventAction)
        }

        viewBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                viewHeight = viewBinding.root.height
            }
        })
    }

    private fun decorActionButton(eventNew: EventNew?, btnAction: Button) {
        @StringRes var btnText: Int? = null
        var clickAction: (() -> Unit)? = null
        var visibility = true
        var mTextSize = 17f
        val userAgreement = eventData?.userAgreement?.uri
        val eventRegistrationState = eventData?.binds?.eventRegistrationState

        when (status) {
            Event.Status.REGISTRATION,
            Event.Status.REGISTRATION_FINISHED,
            Event.Status.RUNNING,
            Event.Status.FINISHED,
            Event.Status.APPROVED -> {
                if (eventRegistrationState != null) {
                    val actions = eventRegistrationState.availableActions ?: arrayListOf("")
                    if (eventRegistrationState.prohibitions?.registrationClosed == false) {
                        when (actions.firstOrNull()) {
                            "register" -> {
                                btnText = R.string.event_action_participate
                                clickAction = {
                                    eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                        if (userAgreement.isNullOrEmpty()) {
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
                                btnText = R.string.event_action_cancel_request
                                clickAction = {
                                    eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                        clickListener.onActionCancel()
                                    }
                                }
                            }
                            else -> {
                                visibility = false
                            }
                        }
                    }
                }
            }
            else -> {
                val actions = eventRegistrationState?.availableActions ?: arrayListOf("")
                if (actions.firstOrNull() == "subscribe" || actions.contains("subscribe")) {
                    mTextSize = 16f
                    if (eventNew?.binds?.isUserSubscribed == true) {
                        btnText = R.string.event_action_unsubscribe_request
                        clickAction = {
                            eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                clickListener.onDeleteSubscribeEvent()
                            }
                        }
                    } else {
                        btnText = R.string.event_action_subscribe_request
                        clickAction = {
                            eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                clickListener.onSubscribeEvent()
                            }
                        }
                    }
                } else {
                    visibility = false
                }
            }
        }

        btnAction.apply {
            text = btnText?.let { context.getString(it) }
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

    fun viewHeight(): Int {
        return this.viewHeight
    }

}