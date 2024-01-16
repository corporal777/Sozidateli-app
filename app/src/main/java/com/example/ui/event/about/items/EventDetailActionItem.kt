package com.example.ui.event.about.items

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.widget.Button
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
import com.example.ui.views.dialogs.EventDescriptionBottomSheet
import com.example.util.URLSpanNoUnderline
import com.xwray.groupie.databinding.BindableItem
import onClickListener


class EventDetailActionItem(
    context: Context,
    eventNew: EventNew?,
    val clickListener: OnActionClickListener,
) : BindableItem<ItemEventDetailActionBlockBinding>(-1001L) {

    private var eventData = eventNew
    private val status: Event.Status? = eventData?.status?.value
    private val eventFormat = eventData?.getEventFormat()
    private val eventDescription = getMarkdownFormattedText(context, eventData?.description)

    override fun bind(viewBinding: ItemEventDetailActionBlockBinding, position: Int) {
        viewBinding.apply {
            tvDescription.apply {
                if (eventData?.description.isNullOrEmpty()) isVisible = false
                else {
                    isCanExpand = false
                    originalText = eventDescription ?: ""
                    limitedMaxLines = 4
                    expandAction = SpannableStringBuilder(context.getString(R.string.yet_btn_text))
                    onExpandClick = {
                        showEventDescriptionDialog(context)
                    }
                }
            }

            formatLn.isVisible = !eventFormat?.name.isNullOrEmpty()
            tvFormat.text = eventFormat?.name

            addressLn.isVisible = !eventData?.address?.fullValue.isNullOrEmpty()
            tvAddress.apply {
                if (!eventData?.address?.fullValue.isNullOrEmpty()){
                    text = CustomSpannableString(eventData?.address?.fullValue).apply {
                        setClickSpan(tvAddress){
                            openRoute(context, eventData?.address?.fullValue)
                        }
                        setColorSpan(R.color.bottom_nav_item_selected_color, context)
                    }
                    highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                    movementMethod = LinkMovementMethod.getInstance()
                }
            }

            phoneLn.isVisible = !eventData?.phone.isNullOrEmpty()
            tvPhone.text = eventData?.phone?.firstOrNull()?.value

            emailLn.isVisible = !eventData?.email.isNullOrEmpty()
            tvEmail.text = eventData?.email?.firstOrNull()?.value

            linkLn.isVisible = !eventData?.site.isNullOrEmpty()
            tvLink.text = eventData?.site?.firstOrNull()?.value

            networkLn.isVisible = !eventData?.socialLink.isNullOrEmpty()
            tvSocialNetwork.text = eventData?.socialLink?.firstOrNull()?.value

            decorActionButton(eventData, btnEventAction, tvCancelRegister)
        }
    }

    private fun decorActionButton(eventNew: EventNew?, btnAction: Button, tvCancel: TextView) {
        var clickAction: (() -> Unit)? = null
        var btnText = R.string.event_action_participate
        var btnTextSize = 17f
        var btnTextColor = R.color.black
        var btnBackground = R.drawable.custom_btn_white_ghost_selectable
        val userAgreement = eventNew?.userAgreement?.uri
        val eventRegistrationState = eventNew?.binds?.eventRegistrationState

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
                                tvCancel.isVisible = false
                                btnText = R.string.event_action_participate
                                btnBackground = R.drawable.custom_btn_white_ghost_selectable
                                btnTextColor = R.color.black
                                clickAction = {
                                    eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                        clickListener.onActionRegister(userAgreement)
                                    }
                                }
                            }
                            "withdraw" -> {
                                tvCancel.isVisible = false
                                btnText = R.string.event_action_cancel_request
                                btnBackground = R.drawable.custom_btn_white_ghost_selectable
                                btnTextColor = R.color.black
                                clickAction = {
                                    eventRegistrationState.prohibitions.profileLevelToLow?.value.checkStateLevel {
                                        clickListener.onActionCancel()
                                    }
                                }
                            }
                            "view" -> {
                                btnText = R.string.event_status_approved
                                btnBackground = R.drawable.btn_background_register_approved
                                btnTextColor = R.color.white
                                tvCancel.apply {
                                    isVisible = true
                                    setOnClickListener {
                                        showCancelRegisterDialog(context, eventRegistrationState)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                tvCancel.isVisible = false
                val actions = eventRegistrationState?.availableActions ?: arrayListOf("")
                if (actions.firstOrNull() == "subscribe" || actions.contains("subscribe")) {
                    btnTextSize = 16f
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
                }
            }
        }

        btnAction.apply {
            text = context.getString(btnText)
            textSize = btnTextSize
            setTextColor(ContextCompat.getColor(context, btnTextColor))
            background = ContextCompat.getDrawable(context, btnBackground)
            onClickListener(clickAction)
            isEnabled = clickAction != null
        }
    }


    private fun showEventDescriptionDialog(context: Context) {
        EventDescriptionBottomSheet(context, eventData?.name, eventData?.description).show()
    }

    private fun showCancelRegisterDialog(
        context: Context,
        eventState: EventRegistrationStateModel,
    ) {
        eventState.prohibitions?.profileLevelToLow?.value.checkStateLevel {
            CancelRegisterEventDialog(context)
                .setCancelRegisterCallback { clickListener.onActionCancel() }
                .show()
        }

    }

    private fun Boolean?.checkStateLevel(hasLevel: () -> Unit) {
        if (this == false) hasLevel()
        else clickListener.onShowUpdateState()
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

    override fun getLayout(): Int = R.layout.item_event_detail_action_block

    interface OnActionClickListener {
        fun onActionRegister(url: String?)
        fun onActionCancel()
        fun onShowUpdateState()
        fun onSubscribeEvent()
        fun onDeleteSubscribeEvent()
    }

    private fun openRoute(context: Context, address: String?) {
        val routeUrl = "https://yandex.ru/maps/?mode=search&text=$address"
        //val routeUrl = "geo:0,0?mode=d&q=$lat,$lon"
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
            val spanned = markWon(context).toMarkdown(description?.replace("\n", " ") ?: "")
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

}