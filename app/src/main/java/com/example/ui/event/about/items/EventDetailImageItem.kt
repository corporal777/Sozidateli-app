package com.example.ui.event.about.items

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemEventDetailMainBinding
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.EventRegistrationStateModel
import com.example.extensions.calendar
import com.example.extensions.daysBetween
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToDefaultDayMonthYearDate
import com.example.extensions.formatToDefaultTime
import com.example.extensions.isSameDay
import com.example.extensions.markWon
import com.example.extensions.onClickListener
import com.example.extensions.parseColor
import com.example.extensions.parseToDate
import com.example.ui.views.CustomSpannableString
import com.example.ui.views.dialogs.CancelRegisterEventBottomSheet
import com.example.ui.views.loading.CustomLoadingButton
import com.example.util.URLSpanNoUnderline
import com.example.util.getColor
import com.example.util.setImage
import com.xwray.groupie.viewbinding.BindableItem

class EventDetailImageItem(
    event: EventNew,
    private val context: Context,
    private val isTemporary: Boolean,
    private val clickListener: OnActionClickListener,
    private val onMoreClick: () -> Unit,
    private val onShowFormResult: () -> Unit
) : BindableItem<ItemEventDetailMainBinding>(1000L) {

    private var eventData = event

    private val imageColor =
        ColorDrawable(event.backgroundColor?.value.parseColor() ?: Color.DKGRAY)

    private val eventDate = getEventDate()
    private val eventDescription = getMarkdownText(eventData.description?.replace("\n", " "))
    private val requestDate = getEventRequestDate()


    private lateinit var mBinding: ItemEventDetailMainBinding
    override fun bind(viewBinding: ItemEventDetailMainBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            tvEventLocation.apply {
                isVisible = !eventData.address?.getShortAddress().isNullOrEmpty()
                text = eventData.address?.getShortAddress()
            }
            tvEventName.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    if ((eventData.name ?: "").length < 120)
                        resources.getDimension(R.dimen.event_detail_name_text_size)
                    else resources.getDimension(R.dimen.event_detail_name_text_size_min)
                )
                text = eventData.name
            }

            tvEventDate.apply {
                isVisible = !eventDate.isNullOrEmpty()
                text = eventDate
            }

            tvEventDescription.apply {
                originalText = eventDescription
            }

            ivLogo.setImage(
                image = eventData.image?.uri ?: imageColor,
            )

            tvShowMore.setOnClickListener {
                onMoreClick.invoke()
            }

            tvCancelRegister.apply {
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }
            decorActionButton(eventData, btnEventAction, tvCancelRegister)
        }
    }

    private fun decorActionButton(
        eventNew: EventNew,
        btnAction: CustomLoadingButton,
        tvCancel: TextView
    ) {
        var clickAction: (() -> Unit)? = null
        var btnText: CharSequence = ""
        var btnBackground = R.drawable.btn_background_green
        val userAgreement = eventNew.userAgreement?.uri
        val state = eventNew.binds?.eventRegistrationState
        val actions = state?.availableActions ?: arrayListOf("")

        var actionText: CharSequence? = null

        if (isTemporary) {
            btnText = getActionButtonText("temporary")
            clickAction = { clickListener.onShowNeedAuth(eventData.id.toString()) }
        } else if (eventNew.isStatusActionAvailable() && state != null) {
            if (actions.contains("register") && !eventNew.isRegistrationClosed()) {
                btnText = getActionButtonText("register")
                clickAction =
                    { state.checkStateLevel { clickListener.onActionRegister(userAgreement) } }
            } else if (actions.contains("withdraw") && !eventNew.isRegistrationClosed()) {
                btnBackground = R.drawable.btn_background_white_ghost
                btnText = getActionButtonText("withdraw")
                clickAction = { state.checkStateLevel { clickListener.onActionCancel() } }
                actionText = getTextShowForm(false, tvCancel)

            } else if (actions.contains("view") && !eventNew.isRegistrationClosed()) {
                btnText = getActionButtonText("view")
                btnBackground = R.drawable.btn_background_register_approved
                actionText = getTextShowForm(true, tvCancel)

            } else if (eventNew.isRegistrationClosed()) {
                btnText = getActionButtonText("closed")
                btnBackground = R.drawable.btn_background_register_closed

            } else btnAction.isVisible = false
        } else if (eventNew.status?.value == Event.Status.CANCELED) {
            btnText = getActionButtonText("canceled")
            btnBackground = R.drawable.btn_background_register_closed
            actionText = getTextShowForm(false, tvCancel)

        } else {
            if (actions.contains("subscribe")) {
                if (eventNew.binds?.isUserSubscribed == true) {
                    btnText = getActionButtonText("unsubscribe")
                    btnBackground = R.drawable.btn_background_white_ghost
                } else btnText = getActionButtonText("subscribe")

                clickAction = {
                    state?.checkStateLevel {
                        clickListener.onSubscribeEvent(eventNew.binds?.isUserSubscribed ?: false)
                    }
                }
            } else btnAction.isVisible = false
        }

        tvCancel.apply {
            text = actionText
            isVisible = !actionText.isNullOrBlank()
        }
        btnAction.apply {
            showProgressLoading(false)
            setProgressColor(R.color.main_brown_color_new)

            setButtonText(btnText)
            setButtonBackground(btnBackground)
            onClickListener(clickAction)
            isEnabled = clickAction != null
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is EventDetailImageItem) return false
        if (eventData != other.eventData) return false
        return true
    }

    override fun bind(
        viewBinding: ItemEventDetailMainBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is EventNew) {
                eventData = payload
                decorActionButton(payload, viewBinding.btnEventAction, viewBinding.tvCancelRegister)
            }
        }
    }


    private fun EventRegistrationStateModel?.checkStateLevel(hasLevel: () -> Unit) {
        if (this?.prohibitions?.profileLevelToLow?.value == false) hasLevel()
        else clickListener.onShowUpdateState()
    }

    private fun getEventDate(): String? {
        val dateStart = eventData.holdingDate?.from ?: return null
        val dateEnd = eventData.holdingDate?.to ?: return null

        if (eventData.isHasOneActivity()) {
            val startDate =
                dateStart.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null
            val finishDate =
                dateEnd.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null

            if (startDate.isSameDay(finishDate)) {
                val firstDate = dateStart.formatToDefaultDayMonthYearDate() + " г."
                val secondDate = dateStart.formatToDefaultTime() + " - " + dateEnd.formatToDefaultTime()
                return "$firstDate, $secondDate"
            } else {
                val firstDate = dateStart.formatToDefaultDayMonthYearDate() + " г., " + dateStart.formatToDefaultTime()
                val secondDate = dateEnd.formatToDefaultDayMonthYearDate() + " г., " + dateEnd.formatToDefaultTime()
                return "$firstDate - $secondDate"
            }
        } else return dateStart.formatToDefaultDayMonthYearDate() + " г." +
                " - " + dateEnd.formatToDefaultDayMonthYearDate() + " г."
    }

    private fun getEventRequestDate(): String? {
        val requestsApply = eventData.requestsApply ?: return null
        if (!requestsApply.dateFrom.isNullOrEmpty() && !requestsApply.dateLimit.isNullOrEmpty()) {
            val today = System.currentTimeMillis()
            val startReq = defaultServerDateTimeFormatter.parse(requestsApply.dateFrom)?.time ?: 0
            if (startReq > today) {
                val day = daysBetween(today, startReq)
                return when (day) {
                    1 -> "До начала приема заявок $day день"
                    in 2..4 -> "До начала приема заявок $day дня"
                    else -> "До начала приема заявок $day дней"
                }
            } else {
                val limitDate = requestsApply.dateLimit.formatToDefaultDayMonthYearDate() + " г."
                val limitTime = requestsApply.dateLimit.formatToDefaultTime()
                return "Заявки принимаются по $limitDate, $limitTime"
            }
        } else return null
    }


    private fun getActionButtonText(description: String?): CharSequence {
        return when (description) {
            "register", "temporary" -> {
                SpannableStringBuilder().apply {
                    append(CustomSpannableString(context.getString(R.string.event_action_participate)).apply {
                        setTextSizeSpan(R.dimen.sub_event_description_text_size, context)
                    })
                    append("\n")
                    append(CustomSpannableString(requestDate).apply {
                        setTextSizeSpan(R.dimen.event_request_date_text_size, context)
                        setColorSpan(R.color.white_70_alpha_color, context)
                    })
                }
            }

            "withdraw" -> {
                CustomSpannableString(context.getString(R.string.event_action_cancel_request)).apply {
                    setColorSpan(R.color.black, context)
                }
            }

            "view" -> context.getString(R.string.event_status_approved)

            "closed" -> {
                CustomSpannableString(context.getString(R.string.event_action_closed_request)).apply {
                    setColorSpan(R.color.event_request_closed_text_color, context)
                }
            }

            "canceled" -> {
                CustomSpannableString(context.getString(R.string.event_status_cancelled)).apply {
                    setColorSpan(R.color.event_request_closed_text_color, context)
                }
            }

            "subscribe" -> context.getString(R.string.event_action_subscribe_request)

            "unsubscribe" -> {
                CustomSpannableString(context.getString(R.string.event_action_unsubscribe_request)).apply {
                    setColorSpan(R.color.black, context)
                }
            }

            else -> ""
        }
    }

    private fun getTextShowForm(withDelimiter: Boolean, textView: TextView): CharSequence? {
        return SpannableStringBuilder().apply {
            if (withDelimiter) {
                append(CustomSpannableString(context.getString(R.string.event_action_cancel_request)).apply {
                    setClickSpan(textView) { showCancelRegisterDialog() }
                })
            }
            if (eventData.isFormEnabled() && eventData.isHasFormResult()) {
                if (withDelimiter) append(" ∙ ")

                append(CustomSpannableString(context.getString(R.string.my_event_form)).apply {
                    setClickSpan(textView) { onShowFormResult.invoke() }
                })
            }
        }
    }

    private fun showCancelRegisterDialog() {
        CancelRegisterEventBottomSheet(context)
            .setCancelRegisterCallback { clickListener.onActionCancel() }
            .show()
    }

    fun getActionButton(): CustomLoadingButton? {
        if (this::mBinding.isInitialized) return mBinding.btnEventAction
        else return null
    }

    private fun getMarkdownText(message: String?): CharSequence? {
        if (message.isNullOrBlank()) return null
        else {
            val spanned = markWon(context).toMarkdown(message)
            return SpannableStringBuilder(spanned).apply {
                val urls = getSpans<URLSpan>()
                urls.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    removeSpan(it)
                    set(start..end, URLSpanNoUnderline(it.url))
                }
            }
        }
    }

    interface OnActionClickListener {
        fun onActionRegister(url: String?)
        fun onActionCancel()
        fun onShowUpdateState()
        fun onSubscribeEvent(subscribe: Boolean)
        fun onShowNeedAuth(eventId: String)
    }

    override fun initializeViewBinding(view: View) = ItemEventDetailMainBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_detail_main
}