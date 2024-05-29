package com.example.ui.notification.items

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.markWon
import com.example.ui.views.CustomSpannableString
import com.example.ui.views.expandableTextView.CustomExpandableTextView
import com.example.util.URLSpanNoUnderline
import com.example.util.getDrawable
import com.xwray.groupie.databinding.BindableItem
import me.saket.bettermovementmethod.BetterLinkMovementMethod

abstract class NotificationItem<T : ViewDataBinding>(
    private val context: Context,
    private val notification: Notification,
    private val listener: OnNotificationActionListener
) : BindableItem<T>(notification.id.toLong()) {

    abstract fun getTitleView(binding: T): TextView
    abstract fun getMessageView(binding: T): CustomExpandableTextView
    abstract fun getReadMoreView(binding: T): View
    abstract fun getBadgeView(binding: T): View
    abstract fun getRootView(binding: T): View

    private var isCollapsed = true
    private var isMessageLong = false
    private val fullMessage = fullMarkdownText(context, notification.message)


    @CallSuper
    override fun bind(viewBinding: T, position: Int) {
        getRootView(viewBinding).apply {
            background = if (!notification.wasRead)
                ContextCompat.getDrawable(context, R.drawable.background_notification_unread)
            else ContextCompat.getDrawable(context, R.drawable.background_notification_normal)
        }
        getBadgeView(viewBinding).apply {
            isVisible = !notification.wasRead
        }
        getTitleView(viewBinding).apply {
            if (notification.eventId != 0 && notification.eventActivityId == 0) {
                text = getNotificationTitle(this)
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                movementMethod = LinkMovementMethod.getInstance()
            } else {
                if (notification.notificationMainType.contentEquals(resources.getString(R.string.notifications_simple_title))) {
                    text = resources.getString(R.string.notifications_simple_title)
                } else {
                    val titleRes = when (notification.type) {
                        Notification.Type.SIMPLE -> R.string.notifications_simple_title
                        Notification.Type.ACCEPTABLE -> R.string.notifications_acceptable_title
                        Notification.Type.RATE -> R.string.notifications_rate_title
                    }

                    text = resources.getString(titleRes)
                }
            }
        }

        getMessageView(viewBinding).apply {
            isVisible = !notification.message.isNullOrEmpty()
            isTextCollapsed = isCollapsed
            limitedMaxLines = 7
            originalText = fullMessage
            onTextExpandableCallback = {
                isMessageLong = it
            }
            onLinkClickListener = {
                listener.onLinkClickListener(it)
            }
        }
        getReadMoreView(viewBinding).apply {
            isVisible = isMessageLong
            changeTextReadMore(!isCollapsed)
            setOnClickListener {
                getMessageView(viewBinding).toggle()
                isCollapsed = !isCollapsed
                changeTextReadMore(!isCollapsed)
                getMessageView(viewBinding).isTextCollapsed = isCollapsed
            }
        }
    }

    private fun View.changeTextReadMore(isExpanded: Boolean) {
        (this as TextView).apply {
            if (isExpanded) text = context.getString(R.string.hide_all_sessions_history)
            else text = context.getString(R.string.notifications_read_more)
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is NotificationItem<*>) return false
        if (notification != other.notification) return false
        return true
    }

    override fun bind(viewBinding: T, position: Int, payloads: MutableList<Any>?) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Notification) {
                getRootView(viewBinding).apply {
                    background = if (!payload.wasRead)
                        getDrawable(R.drawable.background_notification_unread)
                    else getDrawable(R.drawable.background_notification_normal)
                }
                getBadgeView(viewBinding).isVisible = !payload.wasRead
            }
        }
    }


    private fun fullMarkdownText(context: Context, message: String?): SpannableStringBuilder? {
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

    private fun getNotificationTitle(textView: TextView): SpannableStringBuilder? {
        if (notification.eventInfo?.name.isNullOrEmpty()) return null
        else {
            val notificationTitle = SpannableStringBuilder(context.getString(R.string.notification_event_title_new))
            val eventName = CustomSpannableString(notification.eventInfo?.name).apply {
                setClickSpan(textView) {
                    if (notification.eventId != null) {
                        listener.onOpenEventClickListener(notification.eventId.toString())
                    }
                }
                setColorSpan(R.color.main_brown_color_new, textView.context)
            }
            return notificationTitle.append("\n").append(eventName)
        }
    }


    interface OnNotificationActionListener {
        fun onReadClickListener(id: Int)
        fun onRateClickListener(rateId: String)
        fun onLinkClickListener(url: String)
        fun onOpenEventClickListener(eventId: String)
        fun onAcceptClickListener(notification: Notification, isAccept: Boolean)
    }
}
