package com.example.adapters.notification

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.examle.data.models.NotificationLocal
import com.example.app.R
import com.example.common.dateFormatterFullMothFullYear
import com.example.common.defaultServerDateFormatter
import com.example.common.parseAndFormat
import com.example.extensions.getColor
import com.example.extensions.getDrawable
import com.example.extensions.markWon
import com.example.ui.views.CustomSpannableString
import com.example.ui.views.expandableTextView.CustomExpandableTextView
import com.example.util.URLSpanNoUnderline

abstract class NotificationVH<T : ViewBinding>(
    itemView: View,
    private val listener: OnNotificationActionListener
) : RecyclerView.ViewHolder(itemView) {

    abstract fun getTitleView(binding: T): TextView
    abstract fun getMessageView(binding: T): CustomExpandableTextView
    abstract fun getBadgeView(binding: T): View
    abstract fun getRootView(binding: T): View
    abstract fun getDateTitleView(binding: T): TextView


    private var isCollapsed = true

    fun bindViews(viewBinding: T, notification: NotificationLocal) {
        getDateTitleView(viewBinding).apply {
            isVisible = !notification.titleDate.isNullOrEmpty()
            text = if (notification.titleDate.isNullOrEmpty()) ""
            else notification.titleDate!!.parseAndFormat(
                defaultServerDateFormatter,
                dateFormatterFullMothFullYear
            )
        }

        getRootView(viewBinding).apply {
            background = getDrawable(
                if (!notification.wasRead) R.drawable.background_notification_unread
                else R.drawable.background_notification_normal
            )
        }
        getBadgeView(viewBinding).isVisible = !notification.wasRead

        getTitleView(viewBinding).apply {
            if (notification.eventId != 0 && !notification.eventInfo.isNullOrEmpty()) {
                text = getNotificationTitle(notification)
                highlightColor = getColor(R.color.profile_id_text)
                movementMethod = LinkMovementMethod.getInstance()
            } else text = notification.notificationType
        }

        getMessageView(viewBinding).apply {
            isVisible = !notification.message.isNullOrEmpty()
            isTextCollapsed = isCollapsed
            limitedMaxLines = 7
            originalText = fullMarkdownText(context, notification.message)
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

    private fun TextView.getNotificationTitle(notification: NotificationLocal): SpannableStringBuilder? {
        val notificationTitle =
            SpannableStringBuilder(context.getString(R.string.notification_event_title_new))
        val eventName = CustomSpannableString(notification.eventInfo).apply {
            setClickSpan(this@getNotificationTitle) {
                if (notification.eventId != null) {
                    listener.onOpenEventClick(notification.eventId.toString())
                }
            }
            setColorSpan(R.color.main_brown_color_new, context)
        }
        return notificationTitle.append("\n").append(eventName)
    }

    interface OnNotificationActionListener {
        fun onReadClick(id: Int)
        fun onRateClick(rateId: String)
        fun onLinkClick(url: String)
        fun onOpenEventClick(eventId: String)
        fun onAcceptClick(notification: NotificationLocal, isAccept: Boolean)
    }
}