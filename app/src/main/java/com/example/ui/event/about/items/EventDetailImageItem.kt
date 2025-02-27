package com.example.ui.event.about.items

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Log
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
import com.example.extensions.setArgument
import com.example.extensions.setColorSpan
import com.example.extensions.setTextCustomSize
import com.example.extensions.setTextSizeSpan
import com.example.holders.redesign.CustomBindingItem
import com.example.ui.event.formResult.EventFormResultFragment
import com.example.ui.event.formResult.EventFormResultFragment.Companion.EVENT_FORM_FRAGMENT_TAG
import com.example.ui.main.MainActivity
import com.example.ui.views.CustomSpannableString
import com.example.ui.views.dialogs.CancelRegisterEventBottomSheet
import com.example.ui.views.loading.CustomLoadingButton
import com.example.util.URLSpanNoUnderline
import com.example.util.getColor
import com.example.util.setImage
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class EventDetailImageItem(
    event: EventNew,
    private val context: Context,
    private val isTemporary: Boolean,
    private val clickListener: OnActionClickListener,
    private val onMoreClick: () -> Unit,
    private val onShowFormResult: () -> Unit
) : CustomBindingItem<ItemEventDetailMainBinding>(1000L) {

    private var eventData = event
    private val eventDate = getEventDate()
    private val eventDescription = getMarkdownText(eventData.description, context, true)
    private val requestDate = getEventRequestDate()
    private val eventImage: Any? = if (!eventData.image?.uri.isNullOrEmpty()) eventData.image?.uri
    else ColorDrawable(eventData.backgroundColor?.value.parseColor() ?: Color.DKGRAY)


    override fun bind(viewBinding: ItemEventDetailMainBinding, position: Int) {
        viewBinding.apply {
            tvEventLocation.apply {
                isVisible = !eventData.address?.getShortAddress().isNullOrEmpty()
                text = eventData.address?.getShortAddress()
            }
            tvEventName.apply {
                if ((eventData.name ?: "").length < 120)
                    setTextCustomSize(R.dimen.event_detail_name_text_size)
                else setTextCustomSize(R.dimen.event_detail_name_text_size_min)
                text = eventData.name
            }
            tvEventDate.apply {
                isVisible = !eventDate.isNullOrEmpty()
                text = eventDate
            }
            tvEventDescription.originalText = eventDescription
            ivLogo.setImage(eventImage, 300)
            tvShowMore.setOnClickListener { onMoreClick.invoke() }
            tvCancelRegister.apply {
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }
            btnEventAction.setActionButton(tvCancelRegister)
        }
    }

    private fun CustomLoadingButton.setActionButton(tvCancel: TextView) {
        val state = eventData.binds?.currentUserRegistrationState
        val actions = state?.availableActions ?: arrayListOf("")

        var clickAction: (() -> Unit)? = null
        var actionText: CharSequence? = null
        var btnText: CharSequence = ""
        var btnBackground = R.drawable.btn_background_green

        if (isTemporary) {
            btnText = getActionButtonText("temporary")
            clickAction = { clickListener.onShowNeedAuth() }
        } else if (eventData.isStatusActionAvailable() && state != null) {
            if (actions.contains("register") && !eventData.isRegistrationClosed()) {
                btnText = getActionButtonText("register")
                clickAction = { checkStateLevel { clickListener.onActionRegister() } }

            } else if (actions.contains("withdraw") && !eventData.isRegistrationClosed()) {
                btnBackground = R.drawable.btn_background_white_ghost
                btnText = getActionButtonText("withdraw")
                clickAction = { checkStateLevel { clickListener.onActionCancel() } }
                actionText = getTextShowForm(false, tvCancel)

            } else if (actions.contains("view") && !eventData.isRegistrationClosed()) {
                btnText = getActionButtonText("view")
                btnBackground = R.drawable.btn_background_register_approved
                actionText = getTextShowForm(true, tvCancel)

            } else if (eventData.isRegistrationClosed()) {
                btnText = getActionButtonText("closed")
                btnBackground = R.drawable.btn_background_register_closed

            } else isVisible = false
        } else if (eventData.status?.value == Event.Status.CANCELED) {
            btnText = getActionButtonText("canceled")
            btnBackground = R.drawable.btn_background_register_closed
            actionText = getTextShowForm(false, tvCancel)

        } else {
            if (actions.contains("subscribe")) {
                val subscribed = eventData.binds?.isUserSubscribed ?: false
                if (subscribed) {
                    btnText = getActionButtonText("unsubscribe")
                    btnBackground = R.drawable.btn_background_white_ghost
                } else btnText = getActionButtonText("subscribe")

                clickAction = { checkStateLevel { clickListener.onSubscribeEvent(subscribed) } }
            } else isVisible = false
        }

        tvCancel.apply {
            text = actionText
            isVisible = !actionText.isNullOrBlank()
        }

        showProgressLoading(false)
        setProgressColor(R.color.main_brown_color_new)
        setButtonText(btnText)
        setButtonBackground(btnBackground)
        onClickListener(clickAction)
        isEnabled = clickAction != null
    }

    private fun CustomLoadingButton.checkStateLevel(hasLevel: () -> Unit) {
        val state = eventData.binds?.currentUserRegistrationState
        if (state?.prohibitions?.profileLevelToLow?.value == false) {
            showProgressLoading(true)
            hasLevel()
        }
        else clickListener.onShowUpdateState()
    }


    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is EventDetailImageItem) return false
        if (eventData != other.eventData) return false
        return true
    }

    override fun bind(binding: ItemEventDetailMainBinding, payload: Any) {
        if (payload is EventNew) {
            eventData = payload
            binding.btnEventAction.setActionButton(binding.tvCancelRegister)
        }
    }

    private fun showCancelRegisterDialog() {
        CancelRegisterEventBottomSheet(context)
            .setCancelRegisterCallback { clickListener.onActionCancel() }
            .show()
    }


    private fun getEventDate(): String? {
        val dateStart = eventData.holdingDate?.from ?: return null
        val dateEnd = eventData.holdingDate?.to ?: return null

        if (eventData.isHasOneActivity()) {
            val startDate = dateStart.calendar(defaultServerDateFormatter) ?: return null
            val finishDate = dateEnd.calendar(defaultServerDateFormatter) ?: return null

            if (startDate.isSameDay(finishDate)) {
                val firstDate = dateStart.formatToDefaultDayMonthYearDate() + " г."
                val firstTime = dateStart.formatToDefaultTime()
                val secondTime = dateEnd.formatToDefaultTime()
                return if (firstTime.isNullOrEmpty() || secondTime.isNullOrEmpty()) firstDate
                else "$firstDate, $firstTime - $secondTime"
            } else {
                val firstTime = dateStart.formatToDefaultTime() ?: ""
                val firstDate = dateStart.formatToDefaultDayMonthYearDate() + " г., " + firstTime
                val secondTime = dateEnd.formatToDefaultTime() ?: ""
                val secondDate = dateEnd.formatToDefaultDayMonthYearDate() + " г., " + secondTime
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
        val string: (Int) -> String = { res -> context.getString(res) }
        return when (description) {
            "register", "temporary" -> {
                SpannableStringBuilder().apply {
                    append(SpannableString(string(R.string.event_action_participate)).setTextSizeSpan(R.dimen.sub_event_description_text_size, context))
                    append(SpannableString("\n" + requestDate).setTextSizeSpan(R.dimen.event_request_date_text_size, context).setColorSpan(R.color.white_70_alpha_color, context))
                }
            }

            "withdraw" ->
                SpannableString(string(R.string.event_cancel_request)).setColorSpan(R.color.black, context)

            "closed" ->
                SpannableString(string(R.string.event_closed_request)).setColorSpan(R.color.request_closed_text_color, context)

            "canceled" ->
                SpannableString(string(R.string.event_status_cancelled)).setColorSpan(R.color.request_closed_text_color, context)

            "unsubscribe" ->
                SpannableString(string(R.string.event_unsubscribe_request)).setColorSpan(R.color.black, context)

            "view" -> string(R.string.event_status_approved)
            "subscribe" -> string(R.string.event_subscribe_request)
            else -> ""
        }
    }

    private fun getTextShowForm(withDelimiter: Boolean, textView: TextView): CharSequence? {
        return SpannableStringBuilder().apply {
            if (withDelimiter) {
                append(CustomSpannableString(context.getString(R.string.event_cancel_request)).apply {
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

    interface OnActionClickListener {
        fun onActionRegister()
        fun onActionCancel()
        fun onShowUpdateState()
        fun onSubscribeEvent(subscribe: Boolean)
        fun onShowNeedAuth()
    }

    override fun initializeViewBinding(view: View) = ItemEventDetailMainBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_detail_main
}