package com.example.holders

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.parseAsHtml
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseAndFormat
import com.example.extensions.substringToWholeWord
import com.example.util.DATE_TIME_FORMAT_DEFAULT_FULL_MONTH
import com.example.util.URLSpanNoUnderline
import com.example.util.markWon
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import maxLength
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import java.text.SimpleDateFormat
import java.util.*

abstract class NotificationItem(
    private val notification: Notification,
    private val onReadMoreClickListener: OnNotificationReadMoreClickListener,
    private val onLinkClickListener: BetterLinkMovementMethod.OnLinkClickListener,
    private val openEventListener: OnOpenEventListener
) : Item(notification.id.toLong()) {

    abstract fun getTitleView(viewHolder: GroupieViewHolder): TextView
    abstract fun getMessageView(viewHolder: GroupieViewHolder): TextView
    abstract fun getDateView(viewHolder: GroupieViewHolder): TextView
    abstract fun getReadMoreView(viewHolder: GroupieViewHolder): View

    @CallSuper
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (notification.wasRead) R.color.notification_center_notification_read else R.color.notification_center_notification_unread
                )
            )
        }

        getTitleView(viewHolder).apply {
            if (notification.eventId != 0 && notification.eventActivityId == 0) {
                text = context.resources.getString(
                    R.string.notification_event_title,
                    "<br><br><a href=" + notification.eventInfo?.link + " target=_blank>«" + notification.eventInfo?.name + "»</a>"
                ).parseAsHtml()
                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { _, url ->
                        if (notification.eventId != null) {
                            openEventListener(notification.eventId.toString())
                        }
                        true
                    }
                removeUrlUnderline()
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

        getMessageView(viewHolder).apply {
            isVisible = !notification.message.isNullOrEmpty()
            //val message = notification.message?.parseAsHtml()
            //val ellipsizedMessage = message?.substringToWholeWord(maxLength)
            //text = ellipsizedMessage
            //getReadMoreView(viewHolder).isVisible = ellipsizedMessage != message
            val note = ellipsizeMarkdownText(context, notification.message)
            text = note.second
            getReadMoreView(viewHolder).isVisible = note.first
            BetterLinkMovementMethod.linkifyHtml(this)
                .setOnLinkClickListener(onLinkClickListener)
        }

        getDateView(viewHolder).apply {
            val parsedDate = notification.date.parseAndFormat(
                defaultServerDateTimeFormatter,
                SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT_FULL_MONTH, Locale.getDefault())
            )
            text = parsedDate
        }

        getReadMoreView(viewHolder).apply {
            setOnClickListener { onReadMoreClickListener(this@NotificationItem.notification.id) }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is NotificationItem) return false
        if (notification != other.notification) return false
        return true
    }


    private fun ellipsizeMarkdownText(context: Context, message : String?): Pair<Boolean, SpannableStringBuilder> {
        val spanned = markWon(context).toMarkdown(message?:"")
        val ellipsizedSpan =  if (spanned.length > 200){
            Pair(true, SpannableStringBuilder(spanned.subSequence(0,200)).append('.').append('.').append('.'))
        } else Pair(false, SpannableStringBuilder(spanned))
        ellipsizedSpan.second.apply {
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


typealias OnNotificationReadMoreClickListener = (id: Int) -> Unit
typealias OnOpenEventListener = (rateId: String) -> Unit