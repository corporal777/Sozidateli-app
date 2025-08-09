package com.example.holders.registerEvent

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.view.View
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventHeaderBinding
import com.example.data.models.EventRegistration
import com.example.common.calendar
import com.example.common.defaultServerDateFormatter
import com.example.common.formatToDefaultDate
import com.example.common.isSameDay
import com.example.extensions.markWon
import com.example.common.parseToDate
import com.example.util.URLSpanNoUnderline
import com.xwray.groupie.viewbinding.BindableItem

class RegisterEventHeaderItem(
    id: Long,
    context: Context,
    private val event: EventRegistration,
) : BindableItem<ItemRegisterEventHeaderBinding>(id) {

    private val eventStartDate = getEventDate()
    private val eventDescription = getEventDescription(context)

    override fun bind(viewBinding: ItemRegisterEventHeaderBinding, position: Int) {
        viewBinding.apply {
            tvFormLabel.apply {
                isVisible = !event.registrationHeadline.isNullOrBlank()
                text = event.registrationHeadline
            }
            tvEventDate.text = eventStartDate
            tvFormDescription.apply {
                isVisible = !eventDescription.isNullOrBlank()
                text = eventDescription
            }
        }
    }

    private fun getEventDate(): String? {
        val dateStart = event.conferenceStart ?: return null
        val dateEnd = event.conferenceFinish ?: return null

        val startDate =
            dateStart.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null
        val finishDate =
            dateEnd.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null

        return "Дата проведения " +
                if (startDate.isSameDay(finishDate)) dateStart.formatToDefaultDate()
                else dateStart.formatToDefaultDate() + " - " + dateEnd.formatToDefaultDate()
    }

    private fun getEventDescription(context : Context) : CharSequence? {
        if (event.registrationSubtitle.isNullOrBlank()) return null
        else {
            val spanned = markWon(context).toMarkdown(event.registrationSubtitle!!)
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

    override fun getLayout(): Int = R.layout.item_register_event_header
    override fun initializeViewBinding(view: View) = ItemRegisterEventHeaderBinding.bind(view)
}