package com.example.ui.event.about.items

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.EventRegistrationStateModel
import com.example.databinding.ItemEventDetailActionBlockBinding
import com.example.extensions.markWon
import com.example.ui.views.CustomSpannableString
import com.example.ui.views.dialogs.CancelRegisterEventDialog
import com.example.ui.views.dialogs.EventDescriptionBottomSheet
import com.example.ui.views.loading.CustomLoadingButton
import com.example.util.URLSpanNoUnderline
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.onClickListener


class EventDetailActionItem(
    eventNew: EventNew,
    private val context: Context,
    private val clickListener: OnActionClickListener,
    private val onShowFormResult: () -> Unit
) : BindableItem<ItemEventDetailActionBlockBinding>(-1001L) {

    private var eventData = eventNew
    private val eventFormat = eventData.getEventFormat()
    private val eventDescription = getMarkdownFormattedText(context, eventData.description)

    private lateinit var mBinding: ItemEventDetailActionBlockBinding
    override fun bind(viewBinding: ItemEventDetailActionBlockBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            tvDescription.apply {
                if (eventData.description.isNullOrEmpty()) isVisible = false
                else {
                    isCanExpand = false
                    originalText = eventDescription ?: ""
                    limitedMaxLines = 4
                    expandAction = SpannableStringBuilder(context.getString(R.string.yet_btn_text))
                    onExpandClick = {
                        showEventDescriptionDialog()
                    }
                }
            }
            tvCancelRegister.apply {
                highlightColor = ContextCompat.getColor(context, R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }

            formatLn.apply {
                isVisible = !eventFormat.name.isNullOrEmpty()
                tvFormat.text = eventFormat.name
            }
            addressLn.apply {
                isVisible = !eventData.address?.fullValue.isNullOrEmpty()
                tvAddress.apply {
                    text = CustomSpannableString(eventData.address?.fullValue).apply {
                        setClickSpan(tvAddress) { openRoute(eventData.address?.fullValue) }
                        setColorSpan(R.color.bottom_nav_item_selected_color, context)
                    }
                    highlightColor =
                        ContextCompat.getColor(context, R.color.event_tabs_text_unchecked)
                    movementMethod = LinkMovementMethod.getInstance()
                }
            }
            phoneLn.apply {
                isVisible = !eventData.phone.isNullOrEmpty()
                tvPhone.text = eventData.phone?.firstOrNull()?.value
            }
            emailLn.apply {
                isVisible = !eventData.email.isNullOrEmpty()
                tvEmail.text = eventData.email?.firstOrNull()?.value
            }
            linkLn.apply {
                isVisible = !eventData.site.isNullOrEmpty()
                tvLink.text = eventData.site?.firstOrNull()?.value
            }
            networkLn.apply {
                isVisible = !eventData.socialLink.isNullOrEmpty()
                tvSocialNetwork.text = eventData.socialLink?.firstOrNull()?.value
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
        var btnText = R.string.event_action_participate
        var btnTextColor = R.color.black
        var btnBackground = R.drawable.btn_background_white_ghost
        val userAgreement = eventNew.userAgreement?.uri
        val state = eventNew.binds?.eventRegistrationState
        val actions = state?.availableActions ?: arrayListOf("")

        var actionText: CharSequence? = null

        if (eventNew.isStatusActionAvailable() && state != null) {
            if (actions.contains("register") && !eventNew.isRegistrationClosed()) {
                btnText = R.string.event_action_participate
                clickAction = { state.checkStateLevel { clickListener.onActionRegister(userAgreement) } }

            } else if (actions.contains("withdraw") && !eventNew.isRegistrationClosed()) {
                btnText = R.string.event_action_cancel_request
                clickAction = { state.checkStateLevel { clickListener.onActionCancel() } }
                actionText = getTextShowForm(false, tvCancel)
            } else if (actions.contains("view") && !eventNew.isRegistrationClosed()) {
                btnText = R.string.event_status_approved
                btnBackground = R.drawable.btn_background_register_approved
                btnTextColor = R.color.white
                actionText = SpannableStringBuilder()
                    .append(getTextCancelRegister(state, tvCancel))
                    .append(getTextShowForm(true, tvCancel))
            } else if (eventNew.isRegistrationClosed()) {
                btnText = R.string.about_event_registration_closed
                actionText = getTextShowForm(false, tvCancel)
            } else {
                actionText = getTextShowForm(false, tvCancel)
                btnAction.isVisible = false
            }
        } else if (eventNew.status?.value == Event.Status.CANCELED) {
            btnText = R.string.event_status_cancelled
            actionText = getTextShowForm(false, tvCancel)
        } else {
            if (actions.firstOrNull() == "subscribe" || actions.contains("subscribe")) {
                if (eventNew.binds?.isUserSubscribed == true) {
                    btnText = R.string.event_action_unsubscribe_request
                    clickAction = { state?.checkStateLevel { clickListener.onDeleteSubscribeEvent() } }
                } else {
                    btnText = R.string.event_action_subscribe_request
                    clickAction = { state?.checkStateLevel { clickListener.onSubscribeEvent() } }
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
            setButtonText(context.getString(btnText))
            setButtonTextColor(btnTextColor)
            setButtonBackground(btnBackground)
            onClickListener(clickAction)
            isEnabled = clickAction != null
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
                eventData = payload
                decorActionButton(payload, viewBinding.btnEventAction, viewBinding.tvCancelRegister)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventDetailActionItem) return false
        if (eventData != other.eventData) return false
        return true
    }


    private fun showEventDescriptionDialog() {
        EventDescriptionBottomSheet(context, eventData.name, eventData.description).show()
    }

    private fun showCancelRegisterDialog() {
        CancelRegisterEventDialog(context)
            .setCancelRegisterCallback { clickListener.onActionCancel() }
            .show()
    }

    private fun EventRegistrationStateModel?.checkStateLevel(hasLevel: () -> Unit) {
        if (this?.prohibitions?.profileLevelToLow?.value == false) hasLevel()
        else clickListener.onShowUpdateState()
    }

    private fun openRoute(address: String?) {
        val routeUrl = "https://yandex.ru/maps/?mode=search&text=$address"
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(routeUrl))
            context.startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(context, R.string.map_route_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun getMarkdownFormattedText(
        context: Context,
        description: String?
    ): SpannableStringBuilder? {
        if (description.isNullOrEmpty()) return null
        else {
            val spanned = markWon(context).toMarkdown(description.replace("\n", " ") ?: "")
            return SpannableStringBuilder(spanned).apply {
                val urls = getSpans<URLSpan>()
                urls.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    removeSpan(it)
                    set(start..end, URLSpanNoUnderline(it.url))
                }
                replace(Regex("[\\t\\n\\r]+"), " ")
            }
        }
    }

    private fun getTextCancelRegister(
        eventState: EventRegistrationStateModel,
        textView: TextView
    ): CharSequence {
        return CustomSpannableString(context.getString(R.string.change_decision)).apply {
            setClickSpan(textView) { eventState.checkStateLevel { showCancelRegisterDialog() } }
        }
    }

    private fun getTextShowForm(withDelimiter: Boolean, textView: TextView): CharSequence? {
        if (!eventData.isFormEnabled()) return ""
        else if (!eventData.isHasFormResult()) return ""
        else return CustomSpannableString(
            if (withDelimiter) " ∙ " + context.getString(R.string.my_event_form)
            else context.getString(R.string.my_event_form)
        ).apply { setClickSpan(textView) { onShowFormResult.invoke() } }
    }

    fun getActionButton(): CustomLoadingButton? {
        if (this::mBinding.isInitialized) return mBinding.btnEventAction
        else return null
    }

    interface OnActionClickListener {
        fun onActionRegister(url: String?)
        fun onActionCancel()
        fun onShowUpdateState()
        fun onSubscribeEvent()
        fun onDeleteSubscribeEvent()
    }

    override fun getLayout(): Int = R.layout.item_event_detail_action_block
}