package com.example.ui.notification.center.redesign.items

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.parseAsHtml
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseAndFormat
import com.example.holders.OnOpenEventListener
import com.example.ui.views.CustomSpannableString
import com.example.util.ClickableSpanNew
import com.example.util.DATE_TIME_FORMAT_DEFAULT_FULL_MONTH
import com.example.util.URLSpanNoUnderline
import com.example.util.markWon
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

abstract class NotificationItemNew<T : ViewDataBinding>(
    private val context: Context,
    private val notification: Notification,
    private val listener : OnNotificationActionListener
) : BindableItem<T>(notification.id.toLong()) {

    abstract fun getTitleView(viewBinding: T): TextView
    abstract fun getMessageView(viewBinding: T): TextView
    abstract fun getReadMoreView(viewBinding: T): View
    abstract fun getBadgeView(viewBinding: T): View


    private var isExpanded = false
    private val isMessageLong = isMessageTooLong(context, notification.message)
    private val fullMessage = fullMarkdownText(context, notification.message)
    private val shortMessage = ellipsizeMarkdownText(context, notification.message)
    private var actualMessage = SpannableStringBuilder()

    init {
        actualMessage = if (isMessageLong) shortMessage
        else fullMessage
    }


    @CallSuper
    override fun bind(viewBinding: T, position: Int) {
        getBadgeView(viewBinding).apply {
            isVisible = !notification.wasRead
        }
        getTitleView(viewBinding).apply {
            if (notification.eventId != 0 && notification.eventActivityId == 0) {
                text = getNotificationTitle(this)
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                movementMethod = LinkMovementMethod.getInstance()
//                text = context.resources.getString(
//                    R.string.notification_event_title,
//                    "<br><br><a href=" + notification.eventInfo?.link + " target=_blank>«" + notification.eventInfo?.name + "»</a>"
//                ).parseAsHtml()
//                BetterLinkMovementMethod.linkifyHtml(this)
//                    .setOnLinkClickListener { _, url ->
//                        if (notification.eventId != null) {
//                            listener.onOpenEventClickListener(notification.eventId.toString())
//                        }
//                        true
//                    }
//                removeUrlUnderline()
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
            text = actualMessage
            BetterLinkMovementMethod.linkifyHtml(this)
                .setOnLinkClickListener { textView, url ->
                    listener.onLinkClickListener(url)
                    true
                }
        }

        getReadMoreView(viewBinding).apply {
            isVisible = isMessageLong
            changeTextReadMore(isExpanded)
            setOnClickListener {
                if (!isExpanded) {
                    actualMessage = fullMessage
                    isExpanded = true
                } else {
                    actualMessage = shortMessage
                    isExpanded = false
                }
                getMessageView(viewBinding).text = actualMessage
                changeTextReadMore(isExpanded)
            }
        }
    }

    private fun View.changeTextReadMore(isExpanded: Boolean) {
        (this as TextView).apply {
            if (isExpanded) {
                text = context.getString(R.string.hide_all_sessions_history)
            } else text = context.getString(R.string.notifications_read_more)
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is NotificationItemNew<*>) return false
        if (notification != other.notification) return false
        return true
    }



    private fun isMessageTooLong(context: Context, message: String?): Boolean {
        return if (message.isNullOrEmpty()) {
            false
        } else {
            val spanned = markWon(context).toMarkdown(message)
            spanned.length > 240
        }
    }

    private fun ellipsizeMarkdownText(context: Context, message: String?): SpannableStringBuilder {
        if (message.isNullOrBlank() || !isMessageLong) {
            return SpannableStringBuilder("")
        } else {
            val spanned = markWon(context).toMarkdown(message)
            val ellipsizedSpan =
                SpannableStringBuilder(spanned.subSequence(0, 240)).append('.').append('.')
                    .append('.')
            ellipsizedSpan.apply {
                val urls = getSpans<URLSpan>()
                urls.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    removeSpan(it)
                    set(start..end, URLSpanNoUnderline(it.url))
                }
            }
            return ellipsizedSpan
        }
    }

    private fun fullMarkdownText(context: Context, message: String?): SpannableStringBuilder {
        return if (message.isNullOrBlank()) {
            SpannableStringBuilder("")
        } else {
            val spanned = markWon(context).toMarkdown(message ?: "")
            SpannableStringBuilder(spanned).apply {
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

    private fun getNotificationTitle(textView : TextView): SpannableStringBuilder {
        val notificationTitle = SpannableStringBuilder(context.getString(R.string.notification_event_title_new))
        val eventName = CustomSpannableString(notification.eventInfo?.name).apply {
            setClickSpan(textView){
                if (notification.eventId != null) {
                    listener.onOpenEventClickListener(notification.eventId.toString())
                }
            }
            setColorSpan(R.color.main_brown_color_new, textView.context)
        }
        return notificationTitle.append("\n").append(eventName)
    }

    interface OnNotificationActionListener {
        fun onReadClickListener(id: Int)
        fun onReadListener(id: Int)
        fun onRateClickListener(rateId: String)
        fun onLinkClickListener(url: String)
        fun onOpenEventClickListener(eventId: String)
        fun onAcceptClickListener(notification: Notification, isAccept: Boolean)
    }
}
